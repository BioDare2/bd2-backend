/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.onto.species;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class SpeciesService {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());        
    
    final Path configFile; 
    
    List<String> species;
    
    @Autowired
    public SpeciesService(@Value("${bd2.onto.species.file:onto/species.txt}") String configPath) {
        this.configFile = Paths.get(configPath);
    
        if (!Files.isRegularFile(configFile))
            throw new IllegalArgumentException("Cannot access species definition file: "+configFile);
        
        log.info("Species service created");
        
        species = readSpecies(configFile);
    }
    
    public List<String> findAll() {
        return new ArrayList<>(species);
    }

    @Scheduled(fixedRate = 1000*60*30, initialDelay = 1000*60*10)    
    public void updateKnown() {
        this.updateKnown(configFile);
    }

    void updateKnown(Path configFile) {
        
        species = readSpecies(configFile);
    }
    
    List<String> readSpecies(Path file) {
        if (!Files.isRegularFile(file))
            return Collections.emptyList();
        
        try(Stream<String> lines = Files.lines(file)) {
          
            return lines.map(String::trim)
                    .filter( s -> !s.isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
                    
                    
        } catch (IOException e) {
            throw new IllegalStateException("Could not read content of the file: "+e.getMessage());
        }
    }
    
}
