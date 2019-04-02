/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.EnvironmentVariables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentsStorage {
    
    final static String EXPERIMENTS_STORAGE_DIR = "experiments";
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final Path experimentsStorageDir;
    
    
    @Autowired
    public ExperimentsStorage(EnvironmentVariables environment) {
        
        this.experimentsStorageDir = environment.storageDir.resolve(EXPERIMENTS_STORAGE_DIR);
        
        if (!Files.isDirectory(experimentsStorageDir)) {
            try {
                Files.createDirectories(experimentsStorageDir);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Cannot create storage dir: "+experimentsStorageDir+": "+ex.getMessage(),ex);
            }
        }
        
        log.info("ExperimentsStorage is using locaction: {}",this.experimentsStorageDir);
        
    }
    

    public Path getExperimentsDir() {
        return this.experimentsStorageDir;
    }
    
    
    public Path getExperimentDir(long id) {
        
        return experimentsStorageDir.resolve(Long.toString(id));
    }



}
