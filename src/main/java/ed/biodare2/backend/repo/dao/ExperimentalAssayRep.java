/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.util.io.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
@CacheConfig(cacheNames = {"ExperimentalAssay"})
public class ExperimentalAssayRep {
    
    final static String ASSAY_DIR = "ASSAY";
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final ExperimentsStorage expStorage;
    final ObjectReader expReader;
    final ObjectWriter expWriter;
    
    protected final FileUtil fileUtil;
    
    final ResourceGuard<Long> guard = new ResourceGuard<>(100);
    
    @Autowired
    public ExperimentalAssayRep(ExperimentsStorage expStorage,@Qualifier("DomMapper") ObjectMapper mapper) {
        
        fileUtil = new FileUtil();
        this.expStorage = expStorage;
        
        this.expReader = mapper.readerFor(ExperimentalAssay.class);
        this.expWriter = mapper.writerFor(ExperimentalAssay.class);
        
        
        log.info("ExperimentAssayRep is using storage: "+this.expStorage.getExperimentsDir());
        
    }
        
    public Stream<Long> getExerimentsIds() {
        try(Stream<Path> files = Files.list(expStorage.experimentsStorageDir)) {
            return files
                .filter( p -> Files.isDirectory(p))
                .map( p -> p.getFileName().toString())
                .map( n -> {
                    try {
                        return Long.parseLong(n);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter( id -> id != null)
                .collect(Collectors.toList()).stream(); //have to collect cause IO stream is being closed here
        } catch (IOException e) {
            log.error("Cannot list experiments : {}",e.getMessage(),e);
            throw new NonTransientDataAccessException("Cannot list experiments: "+e.getMessage(),e) {
            };
        }
    }
    
    private Stream<ExperimentalAssay> findByOwner(BioDare2User user) {
        
        try(Stream<Path> files =  Files.list(expStorage.experimentsStorageDir)) {
                return files
                    .filter( p -> Files.isDirectory(p))
                    .map( p -> getExperimentalAssay(p))
                    .filter( opt_exp -> opt_exp.isPresent())
                    .map( opt_exp -> opt_exp.get())
                    .collect(Collectors.toList()).stream(); //colecting as io stream is getting closed on leaving
        } catch (IOException e) {
            log.error("Cannot list experiments : {}",e.getMessage(),e);
            throw new NonTransientDataAccessException("Cannot list experiments: "+e.getMessage(),e) {
            };
        }
    }
    
    protected long extractId(Path expDir) {
        try {
            return Long.parseLong(expDir.getFileName().toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id part in: "+expDir+" is not a number");
        }
    }
    
    protected Optional<ExperimentalAssay> getExperimentalAssay(Path expDir) {
        long id = extractId(expDir);
        return getExperimentalAssay(expDir,id);
    }
    
    protected Optional<ExperimentalAssay> getExperimentalAssay(Path expDir,long id) {

        Path file = getExperimentalAssayFile(expDir, id);
        if (!Files.isRegularFile(file))
            return Optional.empty();
        
        ExperimentalAssay assay = guard.guard(id, (expId)-> {
            return readFromFile(file);
        });
        return Optional.of(assay);
    }  
    
    protected Path getExperimentalAssayDir(Path expDir) {
        return expDir.resolve(ASSAY_DIR);
    }
    
    protected Path getExperimentalAssayFile(Path expDir,long id) {
        return getExperimentalAssayDir(expDir).resolve(id+".json");
    }    
    
    @Cacheable(unless="#result == null")
    public Optional<ExperimentalAssay> findOne(long id) {
        
        Path expDir = expStorage.getExperimentDir(id);
        return getExperimentalAssay(expDir,id);
    }
    
    @CachePut(key="#exp.getId()")
    public ExperimentalAssay save(ExperimentalAssay exp) {
        
        Path expDir = expStorage.getExperimentDir(exp.getId());
        Path expFile = getExperimentalAssayFile(expDir, exp.getId());
        
        return guard.guard(exp.getId(),(id) -> {
            try {
                if (!Files.isDirectory(expFile.getParent())) {
                    Files.createDirectories(expFile.getParent());
                }

                if (Files.exists(expFile)) {
                    //log.warn("Overwriting exp: {} at {}",exp.getId(),expFile);
                    fileUtil.backup(expFile, expFile.getParent());
                }

            } catch (IOException e) {
                log.error("Cannot create exp assay file {}: {}",expFile,e.getMessage(),e);
                throw new NonTransientDataAccessException("Cannot create experiment resource: "+e.getMessage(),e) {
                };
            }

            writeToFile(exp,expFile);
            return exp;
        });
    }


    protected ExperimentalAssay readFromFile(Path expFile) {
        
        try {
            return expReader.readValue(expFile.toFile());
        } catch (IOException e) {
            log.error("Cannot read experiment from {} : {}",expFile,e.getMessage(),e);
            throw new DataRetrievalFailureException("Cannot read experiment from json: "+e.getMessage(),e);
        }
    }

    protected void writeToFile(ExperimentalAssay exp, Path expFile) {
        try {
            expWriter.writeValue(expFile.toFile(), exp);
        } catch (IOException e) {
            log.error("Cannot write experiment {} to {} : {}",exp.getId(),expFile,e.getMessage(),e);
            throw new NonTransientDataAccessException("Cannot create experiment resource: "+e.getMessage(),e) {
            };
        }
    }


}
