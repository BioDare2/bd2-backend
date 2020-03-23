/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa.dao;

import ed.biodare2.backend.repo.dao.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobIndResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.repo.system_dom.AssayPack;

import ed.biodare2.backend.util.io.FileUtil;
import ed.biodare2.backend.util.xml.XMLUtil;
import ed.robust.dom.data.TimeSeries;

import ed.robust.dom.tsprocessing.StatsEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class PPAArtifactsRep {
    
    
    final static String PPA_DIR = "PPA";
    final static String BACKUP_DIR = "BACKUP";
    //final static String JOBS_FILE = "PPA_JOBS.xml";
    //final static String STATS_FILE = "PPA_STATS.xml";
    //final static String RESULTS_FILE = "PPA_RESULTS.xml";
    //final static String REQUESTS_DIR = "JOB_REQUESTS";
    
    final static String JOBS_DIR = "JOBS";
    final static String JOB_FULL_RESULTS_FILE = "PPA_RESULTS.xml";
    final static String JOB_SIMPLE_RESULTS_FILE = "PPA_SIMPLE_RESULTS.json";
    final static String JOB_GROUPED_RESULTS_FILE = "PPA_GROUPED_RESULTS.json";
    final static String JOB_FULL_STATS_FILE = "PPA_FULL_STATS.xml";
    final static String JOB_SIMPLE_STATS_FILE = "PPA_SIMPLE_STATS.json";
    final static String JOB_SIMPLE_SUMMARY_FILE = "PPA_JOB_SUMMARY.json";
    final static String JOB_FULL_FILE = "PPA_JOB_FULL.xml";
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    
    final ObjectReader groupSummaryReader;
    final ObjectWriter groupSummaryWriter;    
    
    final ObjectReader simpleStatsReader;
    final ObjectWriter simpleStatsWriter;    
    
    final ObjectReader simpleResultsReader;
    final ObjectWriter simpleResultsWriter;        

    final ObjectReader fullResultsReader;
    final ObjectWriter fullResultsWriter;        
    
    final ObjectReader jobSummaryReader;
    final ObjectWriter jobSummaryWriter;  
    
    final ObjectWriter orgResultsWriter;
    
    final ExperimentsStorage expStorage;
    //final ResourceLock<Long> resourceLock = new ResourceLock<>(60);
    
    final FileUtil fileUtil = new FileUtil();
    final XMLUtil xmlUtil = new XMLUtil();
    final ResourceGuard<Long> guard = new ResourceGuard<>(60);
   
    final LoadingCache<ExpJobKey, Path> jobDirCache;
    final LoadingCache<ExpJobKey, Optional<PPAJobSummary>> jobSummaryCache;

    protected static final class ExpJobKey {
        final long expId;
        final UUID jobId;
        final int hash;
        
        ExpJobKey(long expId, UUID jobId) {
            this.expId = expId;
            this.jobId = jobId;
            this.hash = (int)(expId+jobId.hashCode());
        }

        @Override
        public final int hashCode() {
            return hash;
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj instanceof ExpJobKey) {
                ExpJobKey other = (ExpJobKey)obj;
                return (Objects.equals(this.jobId,other.jobId) && this.expId == other.expId);
            } else {
                return false;
            }
        }
     }
    
    @Autowired
    public PPAArtifactsRep(ExperimentsStorage expStorage, @Qualifier("DomMapper") ObjectMapper mapper) {
        this.expStorage = expStorage;
        

        
        this.groupSummaryReader = mapper.readerFor(PPAJobResultsGroups.class);
        this.groupSummaryWriter = mapper.writerFor(PPAJobResultsGroups.class);        
        this.simpleStatsReader = mapper.readerFor(PPAJobSimpleStats.class);
        this.simpleStatsWriter = mapper.writerFor(PPAJobSimpleStats.class);
        this.simpleResultsReader = mapper.readerFor(PPAJobSimpleResults.class);
        this.simpleResultsWriter = mapper.writerFor(PPAJobSimpleResults.class);         
        this.jobSummaryReader = mapper.readerFor(PPAJobSummary.class);
        this.jobSummaryWriter = mapper.writerFor(PPAJobSummary.class); 

        this.fullResultsReader = mapper.readerFor(PPAJobIndResults.class);
        this.fullResultsWriter = mapper.writerFor(PPAJobIndResults.class);         
        
        this.orgResultsWriter = mapper.writerFor(PPAJobResults.class);
        
        jobDirCache = Caffeine.newBuilder()
                .maximumSize(100)
                .build( key -> makeJobDir(key));

        jobSummaryCache = Caffeine.newBuilder()
                .maximumSize(100)
                .build( key -> getJobSummary(key));        
    }
    
    /**
     * for testing
     */
    protected void clearCaches() {
        jobDirCache.invalidateAll();
        jobSummaryCache.invalidateAll();
        
    }
    
    public void clearAllPPAArtefacts(AssayPack exp)   {

        
        guard.guard(exp.getId(),()-> {

            try {
                Path ppaDir = getPPADir(exp);

                
                if (Files.exists(ppaDir.resolve(JOBS_DIR))) {
                    fileUtil.removeRecursively(ppaDir.resolve(JOBS_DIR));
                }
                
                jobSummaryCache.invalidateAll();
                
            } catch (IOException e) {
                throw new ServerSideException("Cannot clear containers: "+e.getMessage(),e);
            }

        });
        
    }

    public void deleteJobArtefacts(AssayPack exp, UUID jobId) {
        
        guard.guard(exp.getId(),()-> {
             
            deleteJobDir(jobId, exp.getId());
            
            jobSummaryCache.invalidate(new ExpJobKey(exp.getId(), jobId));
        });
    }
    
    
    public Path saveJC2JobRawResults(PPAJobResults results, UUID jobId, AssayPack exp,boolean overwrite)  {
        
        if (!jobId.equals(results.jobId)) {
            throw new IllegalArgumentException("Ids mismatch "+results.jobId+"!="+jobId);
        }
        
        return guard.guard(exp.getId(),(id)-> {

        
        try {
        
            Path jobDir = getJobDir(exp.getId(), jobId);
            
            String fName = "res."+jobId+".json";
            Path file = jobDir.resolve(fName);
            
            
            if (!overwrite && Files.exists(file)) throw new IOException("Results file already exists: "+file);

            orgResultsWriter.writeValue(file.toFile(), results);
            
            return file;
        
            } catch (IOException e) {
                throw new ServerSideException("Cannot save jobsresults containers: "+e.getMessage(),e);
            }
        });
    
    }
    
    public void saveFits(Map<Long, TimeSeries> fits, UUID jobId, AssayPack exp)  {
        
        guard.guard(exp.getId(),()-> {
            
            try {

        
            //Path ppaDir = getPPADir(exp);
            Path jobDir = getJobDir(exp.getId(), jobId);
            
            String fName = "fit."+jobId+".ser";
            Path file = jobDir.resolve(fName);
            
                try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {

                    out.writeObject(fits);
                }
            
            } catch (IOException e) {
                throw new ServerSideException("Cannot save fits: "+e.getMessage(),e);
            }
        });
    }   

    public Optional<Map<Long, TimeSeries>> getFits(UUID jobId, AssayPack exp) {
        
        return guard.guard(exp.getId(),(id)-> {
            
            //Path ppaDir = getPPADir(exp);
            Path jobDir = getJobDir(exp.getId(), jobId);
            
            String fName = "fit."+jobId+".ser";
            Path file = jobDir.resolve(fName);

            if (!Files.exists(file))
                return Optional.<Map<Long, TimeSeries>>empty();
            
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                
                Map<Long, TimeSeries> map = (Map<Long, TimeSeries>)in.readObject();
                return Optional.of(map);
                
            } catch (IOException|ClassNotFoundException e) {
                throw new ServerSideException("Cannot read fits: "+e.getMessage(),e);
            }
        });
    }    
    
    
    public List<PPAJobSummary> getJobsSummaries(AssayPack exp) {
        
        Path jobsDir = getPPADir(exp).resolve(JOBS_DIR);
        if (!Files.isDirectory(jobsDir)) return Collections.emptyList();
        
        try(Stream<Path> jobsDirs = Files.list(jobsDir)) {
            
            return jobsDirs
                    .filter( f -> Files.isDirectory(f))
                    .map( f -> f.getFileName().toString())
                    .map( jobId -> {
                            try {
                                return UUID.fromString(jobId);
                            } catch (IllegalArgumentException e) {
                                return UUID.randomUUID();
                            }
                         })
                    .map( jobId -> getJobSummary(exp, jobId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing( (PPAJobSummary s) -> s.submitted).reversed())
                    .collect(Collectors.toList());
                    
        } catch (IOException e) {
            throw new ServerSideException("Could not list jobs directories in: "+exp.getId()+": "+e.getMessage());
        }
    }
    
    
    public void saveJobFullStats(StatsEntry stats, AssayPack exp, UUID jobId)  {
        
        if (!jobId.equals(stats.getUuid())) {
            throw new IllegalArgumentException("Ids mismatch "+stats.getUuid()+"!="+jobId);
        }
        
        guard.guard(exp.getId(),()-> {
            
        //try {
        
            //Path ppaDir = getPPADir(exp);
            Path jobStatsFile = jobFullStatsFile(exp.getId(), jobId);
            
            
            //stats.setJobId(jobId);
        
            //simpleStatsWriter.writeValue(jobStatsFile.toFile(),stats);
            saveToXMLFile(stats, jobStatsFile);
        
            /*} catch (IOException e) {
                throw new ServerSideException("Cannot save job simple stats: "+e.getMessage(),e);
            }*/
        });
    }    
    
    public void saveJobSummary(PPAJobSummary job, AssayPack exp)  {
        
        if (job.parentId != exp.getId()) {
            throw new IllegalArgumentException("Ids mismatch "+job.parentId+"!="+exp.getId());
        }
        
        guard.guard(exp.getId(),()-> {
            saveJobSummary(job, exp.getId());
            jobSummaryCache.put(new ExpJobKey(exp.getId(), job.jobId), Optional.of(job));
        });
    }    
    
    private void saveJobSummary(PPAJobSummary job, long expId)  {
        
        try {
            
            Path jobFile = jobSummaryFile(expId, job.jobId);
            
        
            jobSummaryWriter.writeValue(jobFile.toFile(),job);
        
            } catch (IOException e) {
                throw new ServerSideException("Cannot save job simple summary: "+e.getMessage(),e);
            }
    }     
    
    public void saveJobSimpleStats(PPAJobSimpleStats stats, AssayPack exp, UUID jobId)  {
        
        if (!jobId.equals(stats.jobId)) {
            throw new IllegalArgumentException("Ids mismatch "+stats.jobId+"!="+jobId);
        }
        
        guard.guard(exp.getId(),()-> {
            
        try {
        
            Path jobStatsFile = jobSimpleStatsFile(exp.getId(), jobId);
                       
            simpleStatsWriter.writeValue(jobStatsFile.toFile(),stats);
        
            } catch (IOException e) {
                throw new ServerSideException("Cannot save job simple stats: "+e.getMessage(),e);
            }
        });
    }    

    public void saveJobSimpleResults(PPAJobSimpleResults res, AssayPack exp, UUID jobId)  {
        
        if (!jobId.equals(res.jobId)) {
            throw new IllegalArgumentException("Ids mismatch "+res.jobId+"!="+jobId);
        }
        
        guard.guard(exp.getId(),()-> {
            
        try {
        
            Path resFile = jobSimpleResultsFile(exp.getId(), jobId);
            
            simpleResultsWriter.writeValue(resFile.toFile(),res);
        
            } catch (IOException e) {
                throw new ServerSideException("Cannot save job simple results: "+e.getMessage(),e);
            }
        });
    }    
    
    public void saveJobResultsGroups(PPAJobResultsGroups results, AssayPack experiment, UUID jobId) {
        
        if (!jobId.equals(results.jobId)) {
            throw new IllegalArgumentException("Ids mismatch "+results.jobId+"!="+jobId);
        }
        
        guard.guard(experiment.getId(),(id)-> {
          
        try {
        
            Path jobResultsFile = jobGroupedResultsFile(experiment.getId(), jobId);
            
            groupSummaryWriter.writeValue(jobResultsFile.toFile(),results);

        } catch (IOException e) {
            throw new ServerSideException("Cannot save results: "+e.getMessage(),e);
        }
        }); 
            
        
    }
    
    public StatsEntry getJobFullStats(AssayPack exp, UUID jobId)  {

        return guard.guard(exp.getId(),(id)-> {

        
            //Path ppaDir = getPPADir(exp);            
            Path jobStatsFile = jobFullStatsFile(exp.getId(),jobId); 
            if (!Files.exists(jobStatsFile)) {
                log.debug("Stats asked from not existing container in exp: {} {}",exp.getId(), jobId);
                return new StatsEntry(jobId);
            }            
            StatsEntry entry = xmlUtil.readFromFile(jobStatsFile, StatsEntry.class);
            return entry;
        });  
        
    }
    
    public Optional<PPAJobSummary> getJobSummary(AssayPack exp, UUID jobId)  {

        return jobSummaryCache.get(new ExpJobKey(exp.getId(), jobId));
        
    }
    
    
    protected Optional<PPAJobSummary> getJobSummary(ExpJobKey key)  {

        return guard.guard(key.expId,(id)-> {
            Path jobFile = jobSummaryFile(key.expId,key.jobId); 
            return readJobSummary(jobFile);
        });          
    }    
    
    protected Optional<PPAJobSummary> readJobSummary(Path jobFile) {
        try {
        
            if (!Files.exists(jobFile)) {
                return Optional.<PPAJobSummary>empty();
            }            
            PPAJobSummary entry = jobSummaryReader.readValue(jobFile.toFile());
            return Optional.of(entry);
        } catch (IOException e) {
            throw new ServerSideException("Cannot read job summary: "+e.getMessage(),e);
        }
        
    }
    
    public PPAJobSimpleStats getJobSimpleStats(AssayPack exp, UUID jobId)  {

        return guard.guard(exp.getId(),(id)-> {

        
        try {
        
            //Path ppaDir = getPPADir(exp);            
            Path jobStatsFile = jobSimpleStatsFile(exp.getId(),jobId); 
            if (!Files.exists(jobStatsFile)) {
                log.warn("Stats asked from not existing container in exp: {} {}",exp.getId(), jobId);
                return new PPAJobSimpleStats(jobId);
            }            
            PPAJobSimpleStats entry = simpleStatsReader.readValue(jobStatsFile.toFile());
            return entry;
        } catch (IOException e) {
            throw new ServerSideException("Cannot read stats: "+e.getMessage(),e);
        }
        });  
        
    }

    public PPAJobSimpleResults getJobSimpleResults(AssayPack exp, UUID jobId)  {

        return guard.guard(exp.getId(),(id)-> {

        
        try {
        
            //Path ppaDir = getPPADir(exp);            
            Path resFile = jobSimpleResultsFile(exp.getId(),jobId); 
            if (!Files.exists(resFile)) {
                log.warn("Results asked from not existing container in exp: {} {}",exp.getId(), jobId);
                return new PPAJobSimpleResults(jobId);
            }            
            PPAJobSimpleResults entry = simpleResultsReader.readValue(resFile.toFile());
            return entry;
        } catch (IOException e) {
            throw new ServerSideException("Cannot read results: "+e.getMessage(),e);
        }
        });  
        
    }    

    public PPAJobResultsGroups getJobResultsGroups(AssayPack experiment, UUID jobId) {
        return guard.guard(experiment.getId(),(id)-> {
          
        try {
        
            //Path ppaDir = getPPADir(experiment);
            Path jobResultsFile = jobGroupedResultsFile(experiment.getId(), jobId);
            if (!Files.exists(jobResultsFile)) return new PPAJobResultsGroups(jobId);
            
            PPAJobResultsGroups entry = groupSummaryReader.readValue(jobResultsFile.toFile());
            return entry;

        } catch (IOException e) {
            throw new ServerSideException("Cannot read results: "+e.getMessage(),e);
        }
        }); 
            
        
    }
    
    public void saveJobIndResults(List<PPAFullResultEntry> results, AssayPack exp, UUID jobId) {
        guard.guard(exp.getId(),()-> {
            
        try {
        
            //Path ppaDir = getPPADir(exp);
            Path resFile = jobIndResultsFile(exp.getId(), jobId);
            
            PPAJobIndResults container = new PPAJobIndResults(jobId, results);
            
            fullResultsWriter.writeValue(resFile.toFile(),container);
        
            } catch (IOException e) {
                throw new ServerSideException("Cannot save job ind results: "+e.getMessage(),e);
            }
        });
    }    
    
    public List<PPAFullResultEntry> getJobIndResults(AssayPack exp, UUID jobId) {
        
        return guard.guard(exp.getId(),(id)-> {
            
            try {
                //Path ppaDir = getPPADir(exp);
                Path jobIndResultsFile = jobIndResultsFile(exp.getId(), jobId);

                if (!Files.exists(jobIndResultsFile)) {
                    return Collections.<PPAFullResultEntry>emptyList();
                }

                PPAJobIndResults container = fullResultsReader.readValue(jobIndResultsFile.toFile());
                return container.results;
            } catch (IOException e) {
                throw new ServerSideException("Cannot read results: "+e.getMessage(),e);
            }        
        });
    }    
    
    protected Path getPPADir(AssayPack exp) {
        
        return getPPADir(exp.getId());
    } 
    
    protected Path getPPADir(long expId) {
        try {
            Path p = expStorage.getExperimentDir(expId).resolve(PPA_DIR);
            if (!Files.isDirectory(p))
                Files.createDirectories(p);
            return p;
        } catch (IOException e) {
            throw new ServerSideException("Cannot access ppa dir for exp:"+expId+"; "+e.getMessage(),e);
        }
    }     
    
    protected Path makeJobDir(ExpJobKey key) {
        
        Path ppaDir = getPPADir(key.expId);
        Path jobDir = ppaDir.resolve(JOBS_DIR).resolve(""+key.jobId);
        try {
            if (!Files.isDirectory(jobDir)) {
                Files.createDirectories(jobDir);
            }
            return jobDir;
        } catch (IOException e) {
            throw new ServerSideException("Cannot make jobDir: "+e.getMessage(),e);
        }
    }
    
    protected Path getJobDir(long expId, UUID jobId) {
        return jobDirCache.get(new ExpJobKey(expId, jobId));
    }   
    
    protected Path jobGroupedResultsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_GROUPED_RESULTS_FILE);
    }    
    
    protected Path jobSummaryFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_SIMPLE_SUMMARY_FILE);
    }
    
    protected Path jobSimpleStatsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_SIMPLE_STATS_FILE);
    }
    
    protected Path jobSimpleResultsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_SIMPLE_RESULTS_FILE);
    }    
    
    protected Path jobFullStatsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_FULL_STATS_FILE);
    }     
    
    protected Path jobIndResultsFile(long expId, UUID jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_FULL_RESULTS_FILE);
    }    
    
    

    protected void saveToXMLFile(Object elm, Path file) {
        xmlUtil.saveToFile(elm,file);
    }


    protected void deleteJobDir(UUID jobId, long expId) {
        
        guard.guard(expId,()-> {
        
        try {
        
            Path jobDir = getJobDir(expId, jobId);

            if (Files.exists(jobDir)) {
                fileUtil.removeRecursively(jobDir);
            }
            
            } catch (IOException e) {
                throw new ServerSideException("Cannot delete job dir: "+jobId+": "+e.getMessage(),e);
            }
        });
    }



















    
}
