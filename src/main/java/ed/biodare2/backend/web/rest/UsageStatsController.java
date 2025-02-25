/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.features.tsdata.sorting.TSSortOption;
import ed.biodare2.backend.features.tsdata.sorting.TSSortParams;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.handlers.ExperimentDataHandler;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Zielu
 */
@RestController
@RequestMapping("api/usage")
public class UsageStatsController extends BioDare2Rest {

    private static final Logger log = LoggerFactory.getLogger(UsageStatsController.class);
    
    final ExperimentalAssayRep expRep;
    final ExperimentPackHub expPacks;
    final ExperimentDataHandler dataHandler;
    final UserAccountRep accounts;
    final ObjectMapper objectMapper;
    @Value("${bd2.usagestats.file:usage_stats.json}") String jsonFile;
    final Path jsonFilePath;

    UsageStatsController(ExperimentalAssayRep expRep, ExperimentPackHub expPacks, ExperimentDataHandler dataHandler, UserAccountRep accounts,
                         @Value("${bd2.usagestats.file:usage_stats.json}") String jsonFile) {
        this.expRep = expRep;
        this.expPacks = expPacks;
        this.dataHandler = dataHandler;
        this.accounts = accounts;
        this.objectMapper = new ObjectMapper();
        log.debug("jsonFile value: {}", jsonFile);
        this.jsonFilePath = Paths.get(jsonFile);
    }
    
    @RequestMapping(value="data",method = RequestMethod.GET)
    //public ListWrapper<String> dataStats(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
    public Map<String, Map<String, Integer>> dataStats(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("dataStats: {}", currentUser);
        
        if (!currentUser.getLogin().equals("demo") && !currentUser.getLogin().equals("test"))
            throw new InsufficientRightsException("Only demo and test users can call it");
        
        Stream<List<String>> entries = getDataEntries(expRep.getExerimentsIds());

        Map<Pair<String,String>, List<List<String>>> ownerGroups = group(entries);
        
        Map<Pair<String,String>, Integer> setsStats = countSets(ownerGroups); 
        Map<Pair<String,String>, Integer> seriesStats = countSeries(ownerGroups); 
        
        Map<String, Integer> totalSets = totalByYear(setsStats);
        Map<String, Integer> totalSeries = totalByYear(seriesStats);
        
        /*List<String> rows = new ArrayList<>();
        rows.add("SETS");
        rows.addAll(setsStats.entrySet().parallelStream()
                .sorted(Comparator.comparing(e -> e.getKey().getRight()))
                .map( e -> ""+e.getKey().getLeft()+","+e.getKey().getRight()+","+e.getValue())
                .collect(Collectors.toList()));
        rows.add("SERIES");
        rows.addAll(seriesStats.entrySet().parallelStream()
                .sorted(Comparator.comparing(e -> e.getKey().getRight()))
                .map( e -> ""+e.getKey().getLeft()+","+e.getKey().getRight()+","+e.getValue())
                .collect(Collectors.toList()));
        
        return new ListWrapper<>(rows);
        */
        Map<String, Map<String, Integer>> stats = new HashMap<>();
        stats.put("totalSets",totalSets);
        stats.put("totalSeries",totalSeries);
        return stats;
    }   

    @RequestMapping(value="species",method = RequestMethod.GET)
    public Map<String, Map<String, Integer>> speciesStats(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("speciesStats: {}", currentUser);
        
        if (!currentUser.getLogin().equals("demo") && !currentUser.getLogin().equals("test"))
            throw new InsufficientRightsException("Only demo and test users can call it");
        
        Stream<List<String>> entries = getSpeciesEntries(expRep.getExerimentsIds());
        
        Map<String, List<List<String>>> speciesGroups = entries.collect(
                Collectors.groupingByConcurrent( lst -> lst.get(0) ));
        
        Map<String, Integer> setsStats = countSets(speciesGroups); 
        Map<String, Integer> seriesStats = countSeries(speciesGroups); 
        
        Map<String, Map<String, Integer>> stats = new HashMap<>();
        stats.put("speciesSets",setsStats);
        stats.put("speciesSeries",seriesStats);
        return stats;
    }

    @RequestMapping(value="count", method = RequestMethod.GET)
    public Map<String, Integer> countStats() {

        List<List<String>> entries = getDataEntries(expRep.getExerimentsIds()).collect(Collectors.toList());

        int totalSets = (int) entries.size();

        long totalPublicSets = entries.stream()
                .filter( lst -> lst.get(3).equals("public"))
                .count();
    
        int totalSeries = entries.stream()
        .mapToInt(lst -> Integer.parseInt(lst.get(2)))
        .sum();

        int totalPublicSeries = entries.stream()
        .filter(lst -> lst.get(3).equals("public"))
        .mapToInt(lst -> Integer.parseInt(lst.get(2)))
        .sum();

        long totalUsers = accounts.count();
    
        Map<String, Integer> count = new HashMap<>();
        count.put("totalSets", totalSets);
        count.put("totalPublicSets", (int) totalPublicSets);
        count.put("totalSeries", totalSeries);
        count.put("totalPublicSeries", totalPublicSeries);
        count.put("totalUsers", (int) totalUsers);
        return count;
    }

    @RequestMapping(value="get_count", method=RequestMethod.GET)
    public Map<String, Integer> getUsageStats() throws IOException {
        byte[] jsonData = Files.readAllBytes(jsonFilePath);
        return objectMapper.readValue(jsonData, new TypeReference<Map<String, Integer>>(){});
    }
    
    
    Stream<List<String>> getDataEntries(Stream<Long> ids) {
        
        return ids.parallel().map(id -> getDataEntry(id))
                .filter( lst -> !lst.isEmpty());
    }
    
    List<String> getDataEntry(Long id) {
        
        Optional<AssayPack> opt = expPacks.findOne(id);
        if (!opt.isPresent()) return Collections.emptyList();
        
        AssayPack expPack = opt.get();
        String year = ""+expPack.getSystemInfo().provenance.creation.dateTime.getYear();
        String owner = expPack.getSystemInfo().security.owner;
        String isPublic = expPack.getSystemInfo().security.isPublic ? "public" : "private";
        int tsCount = 0;
        Page page = new Page(0,Integer.MAX_VALUE);
        if (expPack.getSystemInfo().experimentCharacteristic.hasTSData) {
            tsCount = dataHandler.getTSData(expPack, DetrendingType.LIN_DTR, page, new TSSortParams(TSSortOption.NONE, true, null))
                    .map( ds -> ds.traces)
                    .orElse(Collections.emptyList()).size();
        }
        
        return Arrays.asList(owner, year, "" + tsCount, isPublic);
    }
    
    Stream<List<String>> getSpeciesEntries(Stream<Long> ids) {
        
        return ids.parallel().map(id -> getSpeciesEntry(id))
                .filter( lst -> !lst.isEmpty());
    }  
    
    List<String> getSpeciesEntry(Long id) {
        
        Optional<AssayPack> opt = expPacks.findOne(id);
        if (!opt.isPresent()) return Collections.emptyList();
        
        AssayPack expPack = opt.get();
        String species = expPack.getAssay().species;
        int tsCount = 0;
        Page page = new Page(0,Integer.MAX_VALUE);        
        if (expPack.getSystemInfo().experimentCharacteristic.hasTSData) {
            
            tsCount = dataHandler.getTSData(expPack, DetrendingType.LIN_DTR, page,new TSSortParams(TSSortOption.NONE, true, null))
                    .map( ds -> ds.traces)
                    .orElse(Collections.emptyList()).size();
        }
        
        return Arrays.asList(species,"1",""+tsCount);
    }    
    
    Map<Pair<String,String>, List<List<String>>> group(Stream<List<String>> entries) {
        
        Map<Pair<String,String>, List<List<String>>> ownerGroups = entries.collect(Collectors.groupingByConcurrent( lst -> new Pair<>(lst.get(0),lst.get(1) )));
        return ownerGroups;
    }

    <K> Map<K, Integer> countSets(Map<K, List<List<String>>> ownerGroups) {

        return ownerGroups.entrySet().parallelStream()
                .map(e -> new Pair<>(e.getKey(), e.getValue().size()))
                .filter( p -> p.getRight() > 0)
                .collect(Collectors.toConcurrentMap(p -> p.getLeft(), p-> p.getRight()));
    }

    <K> Map<K, Integer> countSeries(Map<K, List<List<String>>> ownerGroups) {
        return ownerGroups.entrySet().parallelStream()
                .map(e -> {
                    int series = e.getValue().stream()
                            .mapToInt(lst -> Integer.parseInt(lst.get(2)))
                            .sum();
                    return new Pair<>(e.getKey(), series);
                })
                .filter( p -> p.getRight() > 0)
                .collect(Collectors.toConcurrentMap(p -> p.getLeft(), p-> p.getRight()));
    }

    Map<String, Integer> totalByYear(Map<Pair<String, String>, Integer> userStats) {
        
        ConcurrentMap<String, List<Pair<String, Integer>>> byYear = userStats.entrySet().parallelStream()
                .map( e -> new Pair<>(e.getKey().getRight(),e.getValue()))
                .collect(Collectors.groupingByConcurrent( p-> p.getLeft()));
        
        return byYear.entrySet().stream()
                .map( e -> {
                    int sum = e.getValue().parallelStream()
                            .mapToInt( p -> p.getRight())
                            .sum();
                    return new Pair<>(e.getKey(),sum);        
                })
                .collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()));
                
        
    }
}