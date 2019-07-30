/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.web.rest.ServerSideException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Repository
@CacheConfig(cacheNames = {"RhythmicityArtifacts"})
public class RhythmicityArtifactsRep {

    final static String RHYTHMICITY_DIR = "RHYTHMICITY";
    final static String JOBS_DIR = "JOBS";
    final static String JOB_DETAILS_FILE = "RHYTHMICITY_JOB_DETAILS.json";
    
    final ResourceGuard<Long> guard = new ResourceGuard<>(60);
    final ExperimentsStorage expStorage;
    
    final ObjectReader jobDetailsReader;
    final ObjectWriter jobDetailsWriter;     

    @Autowired
    public RhythmicityArtifactsRep(ExperimentsStorage expStorage,
            @Qualifier("DomMapper") ObjectMapper mapper) {
        this.expStorage = expStorage;
        
        this.jobDetailsReader = mapper.readerFor(RhythmicityJobSummary.class);
        this.jobDetailsWriter = mapper.writerFor(RhythmicityJobSummary.class);         
    }
    
    
    // cache put did not work if method was void
    @CachePut(key="{#exp.getId(),#job.jobId}")
    //@CacheEvict(key="{#exp.getId(),#job.jobId}")
    @Transactional    
    public RhythmicityJobSummary saveJobDetails(RhythmicityJobSummary job, AssayPack exp) {
        
        //System.out.println("\n\n---- Saving "+job.jobId);
        return saveJobDetails(job, exp.getId());
    } 
    
    @Cacheable(key="{#expId,#jobId}",unless="#result == null")
    public Optional<RhythmicityJobSummary> findOne(UUID jobId, long expId) {
        
        //System.out.println("\n\n---- Reading "+jobId);
        return readJobDetails(jobId,expId);
    }    
    
    RhythmicityJobSummary saveJobDetails(RhythmicityJobSummary job, long expId)  {
        
        return guard.guard(expId,(id)-> {
            try {

                Path jobFile = jobDetailsFile(expId, job.jobId);


                jobDetailsWriter.writeValue(jobFile.toFile(),job);

                return job;
            } catch (IOException e) {
                throw new ServerSideException("Cannot save job: "+e.getMessage(),e);
            }
        });        
    }    
    
    protected Path jobDetailsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_DETAILS_FILE);
    }    

    protected Path getJobDir(long expId, UUID jobId) {
        
        try {
            
            Path dir = expStorage.getExperimentDir(expId)
                    .resolve(RHYTHMICITY_DIR)
                    .resolve(JOBS_DIR)
                    .resolve(jobId.toString());
            
            if (!Files.exists(dir))
                Files.createDirectories(dir);
        
            return dir;
        } catch (IOException e) {
            throw new ServerSideException("Cannot get sytem dir: "+e.getMessage(),e);
        }         
    }

    Optional<RhythmicityJobSummary> readJobDetails(UUID jobId, long expId) {
        
       return guard.guard(expId, (id) -> {
            try {

                Path file = jobDetailsFile(expId, jobId);
                if (!Files.exists(file))
                    return Optional.<RhythmicityJobSummary>empty();
                
                RhythmicityJobSummary job = jobDetailsReader.readValue(file.toFile());
                return Optional.of(job);
                
            } catch (IOException e) {
                throw new ServerSideException("Cannot access system info: "+e.getMessage(),e);
            }
       });
    }
    
}
