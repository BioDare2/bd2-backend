/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.features.rdmsocial.RDMAssetsAspect;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.search.ExperimentSearcher;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.security.BioDare2User;
import ed.robust.dom.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Zielu
 */
@RestController
@RequestMapping("api/rdm")
public class RDMSocialStatsController extends BioDare2Rest {
    
    final ExperimentalAssayRep expRep;
    
    final ExperimentPackHub expPacks;
    
    final RDMSocialHandler rdmHandler;
    final ExperimentSearcher searcher;

    @Autowired
    RDMSocialStatsController(ExperimentalAssayRep expRep, ExperimentPackHub expPacks, RDMSocialHandler rdmHandler,
            ExperimentSearcher searcher) {
        this.expRep = expRep;
        this.expPacks = expPacks;
        this.rdmHandler = rdmHandler;
        this.searcher = searcher;
    }
    
    @RequestMapping(value="stats",method = RequestMethod.GET)
    @Transactional  
    public Map<String, Integer> warningStats(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("rdm Stats: {}", currentUser);
        
        if (true) throw new IllegalStateException("Disabled in the code base");
        
        if (!currentUser.getLogin().equals("demo") && !currentUser.getLogin().equals("test"))
            throw new InsufficientRightsException("Only demo and test users can call it");
        
        Stream<List<String>> entries = getRDMEntries(expRep.getExerimentsIds());
        
        //Map<Pair<String,String>, List<List<String>>> cohortGroups = groupCohorts(entries);
        
        Map<Pair<String,String>, List<List<String>>> warningGroups = groupWarnings(entries);
        
        Map<Pair<String,String>, Integer> warningStats = countMembers(warningGroups); 
        
        Map<String, Integer> stats = warningStats.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getLeft()+"_"+e.getKey().getRight(), e -> e.getValue()));
                                ;
        return stats;
        
        /*
        Map<String, Integer> totalSets = totalByYear(setsStats);
        Map<String, Map<String, Integer>> stats = new HashMap<>();
        stats.put("totalSets",totalSets);
        stats.put("totalSeries",totalSeries);
        return stats;*/
    }   

   
    
    Stream<List<String>> getRDMEntries(Stream<Long> ids) {
        
        return ids.map(id -> getRDMEntry(id))
                .filter( lst -> !lst.isEmpty());
    }  
    
    List<String> getRDMEntry(Long id) {
        
        Optional<AssayPack> opt = expPacks.findOne(id);
        if (!opt.isPresent()) return Collections.emptyList();
        
        AssayPack expPack = opt.get();
        
        // lets ignore users with less than 5 experiments, as well as the test ones
        String userLogin = expPack.getDbSystemInfo().getAcl().getOwner().getLogin();
        if (userLogin.startsWith("demo") || userLogin.startsWith("test") || userLogin.startsWith("biodare"))
            return Collections.emptyList();
        
        if (searcher.findAllVisible(expPack.getDbSystemInfo().getAcl().getOwner(),false,0,10).currentPage.length < 5) {
            return Collections.emptyList();
        }
        
        String userCohort = expPack.getDbSystemInfo().getAcl().getOwner().getRdmAspect().getCohort().name();
        
        RDMAssetsAspect rdmAspect = rdmHandler.getAssayRDMAspect(expPack);
        
        String expCohort = rdmAspect.cohort.name();        
        // lets change those those that never seen warning
        if (!rdmAspect.cohort.equals(RDMCohort.CONTROL)) {
            if (rdmAspect.measurementWarnings < 1) // we will change it to control as it looks as if it was like this for the user
                expCohort = RDMCohort.CONTROL.name();                
        }
        
        String atWarning = rdmAspect.measurementAdded ? ""+rdmAspect.measurementAddedAtWarning : ""+Integer.MAX_VALUE;
        String atUpdate = rdmAspect.measurementAdded ? ""+rdmAspect.measurementAddedAtUpdate : ""+Integer.MAX_VALUE;
        
        return List.of(userCohort, expCohort, atWarning, atUpdate, ""+rdmAspect.measurementAdded);
        
    }    
    
    Map<Pair<String,String>, List<List<String>>> groupCohorts(Stream<List<String>> entries) {
        
        Map<Pair<String,String>, List<List<String>>> groups = 
                entries.collect(Collectors.groupingByConcurrent( lst -> new Pair<>(lst.get(0),lst.get(1) )));
        return groups;
    }
    
    Map<Pair<String,String>, List<List<String>>> groupWarnings(Stream<List<String>> entries) {
        
        Map<Pair<String,String>, List<List<String>>> groups = 
                entries.collect(Collectors.groupingByConcurrent( lst -> new Pair<>(lst.get(0)+"_"+lst.get(1), lst.get(2) )));
        return groups;
    }    

    <K> Map<K, Integer> countMembers(Map<K, List<List<String>>> groups) {

        return groups.entrySet().parallelStream()
                .collect(Collectors.toConcurrentMap(e -> e.getKey(), e -> e.getValue().size()));
    }

    
}
