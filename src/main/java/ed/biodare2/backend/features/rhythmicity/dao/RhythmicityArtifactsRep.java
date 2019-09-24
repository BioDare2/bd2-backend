/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.util.io.FileUtil;
import ed.biodare2.backend.web.rest.ServerSideException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    final static String JOB_RESULTS_FILE = "RHYTHMICITY_JOB_RESULTS.json";
    
    final FileUtil fileUtil = new FileUtil();
    final ResourceGuard<Long> guard = new ResourceGuard<>(60);
    final ExperimentsStorage expStorage;
    
    final ObjectReader jobDetailsReader;
    final ObjectWriter jobDetailsWriter;     

    final ObjectReader jobResultsReader;
    final ObjectWriter jobResultsWriter; 
    
    @Autowired
    public RhythmicityArtifactsRep(ExperimentsStorage expStorage,
            @Qualifier("DomMapper") ObjectMapper mapper) {
        this.expStorage = expStorage;
        
        this.jobDetailsReader = mapper.readerFor(RhythmicityJobSummary.class);
        this.jobDetailsWriter = mapper.writerFor(RhythmicityJobSummary.class); 

        this.jobResultsWriter = mapper.writerFor(new TypeReference<JobResults<TSResult<BD2eJTKRes>>>(){});
        this.jobResultsReader = mapper.readerFor(new TypeReference<JobResults<TSResult<BD2eJTKRes>>>(){});
    }
    
    
    // cache put did not work if method was void
    @CachePut(key="{#exp.getId(),#job.jobId, 'job'}")
    //@CacheEvict(key="{#exp.getId(),#job.jobId}")
    @Transactional    
    public RhythmicityJobSummary saveJobDetails(RhythmicityJobSummary job, AssayPack exp) {
        
        //System.out.println("\n\n---- Saving "+job.jobId);
        return saveJobDetails(job, exp.getId());
    } 
    
    @Cacheable(key="{#expId,#jobId, 'job'}",unless="#result == null")
    public Optional<RhythmicityJobSummary> findJob(UUID jobId, long expId) {
        
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
    
    @CachePut(key="{#exp.getId(),#job.jobId, 'results'}")
    @Transactional    
    public JobResults<TSResult<BD2eJTKRes>> saveJobResults(JobResults<TSResult<BD2eJTKRes>> results, 
            RhythmicityJobSummary job, AssayPack exp) {
        
        return saveJobResults(results, job.jobId, exp.getId());
    }   
    
    @Cacheable(key="{#expId,#jobId, 'results'}",unless="#result == null")
    public Optional<JobResults<TSResult<BD2eJTKRes>>> findJobResults(UUID jobId, long expId) {
        
        return readJobResults(jobId,expId);
    }    
    
    protected Path jobDetailsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_DETAILS_FILE);
    }    

    protected Path getRhythmicityDir(long expId) {
        
        Path dir = expStorage.getExperimentDir(expId)
                .resolve(RHYTHMICITY_DIR);
        
        return dir;
    }
    
    protected Path getJobsDir(long expId) {
        
        Path dir = getRhythmicityDir(expId)
                .resolve(JOBS_DIR);
        
        return dir;
    }
    
    protected Path getJobDir(long expId, UUID jobId) {
        
        try {
            
            Path dir = getJobsDir(expId)
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

    JobResults<TSResult<BD2eJTKRes>> saveJobResults(JobResults<TSResult<BD2eJTKRes>> results, UUID jobId, long expId) {
        
        return guard.guard(expId,(id)-> {
            try {

                Path resultsFile = jobResultsFile(expId, jobId);


                jobResultsWriter.writeValue(resultsFile.toFile(),results);

                return results;
            } catch (IOException e) {
                throw new ServerSideException("Cannot save results: "+e.getMessage(),e);
            }
        });         
    }

    Path jobResultsFile(long expId, UUID jobId) {
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_RESULTS_FILE);
    }

    Optional<JobResults<TSResult<BD2eJTKRes>>> readJobResults(UUID jobId, long expId) {
        
       return guard.guard(expId, (id) -> {
            try {

                Path file = jobResultsFile(expId, jobId);
                if (!Files.exists(file))
                    return Optional.empty();
                
                JobResults<TSResult<BD2eJTKRes>> res = jobResultsReader.readValue(file.toFile());
                return Optional.of(res);
                
            } catch (IOException e) {
                throw new ServerSideException("Cannot access results: "+e.getMessage(),e);
            }
       });
    }    

    public List<RhythmicityJobSummary> getJobs(AssayPack exp) {
        return getJobs(exp.getId());
    }
    
    public List<RhythmicityJobSummary> getJobs(long expId) {
        
        Path jobsDir = getJobsDir(expId);
        
        if (!Files.isDirectory(jobsDir)) return Collections.emptyList();
        
        try(Stream<Path> jobsDirs = Files.list(jobsDir)) {
            
            return jobsDirs
                    .filter( f -> Files.isDirectory(f))
                    .map( f -> f.getFileName().toString())
                    .map( jobId -> UUID.fromString(jobId))
                    .map( jobId -> findJob(jobId, expId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing( (RhythmicityJobSummary s) -> s.jobStatus.submitted).reversed())
                    .collect(Collectors.toList());
                    
        } catch (IOException e) {
            throw new ServerSideException("Could not list jobs directories in: "+expId+": "+e.getMessage());
        }        
    }

    @CacheEvict(allEntries = true)
    public void clearAll(AssayPack exp) {
        clearAll(exp.getId());
    }

    protected void clearAll(long expId) {
        
       guard.guard(expId, (id) -> {
            try {

                Path file = getRhythmicityDir(expId);
                if (Files.exists(file)) {
                    fileUtil.removeRecursively(file);
                }
                
            } catch (IOException e) {
                throw new ServerSideException("Cannot clear rhythmicity restults: "+e.getMessage(),e);
            }
       });
    }    
    
}
