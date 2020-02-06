/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.backend.features.ppa.PPAUtils;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.tsdata.datahandling.DataProcessingException;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobIndResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.system_dom.ACLInfo;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.jobcenter.JobsContainer;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.StatsEntry;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class PPATestSeeder {
    
    @Autowired
    ExperimentPackHub expBoundles;
    
    @Autowired
    RDMSocialHandler rdmSocialHandler;

    @Autowired 
    Fixtures fixtures;
    
    @Autowired
    PPAArtifactsRep ppaRep;
    
    @Autowired
    TSDataHandler tsHandler;
    
    
    @Autowired
    public ObjectMapper mapper;
    
    
    public AssayPack insertExperiment() {
        return insertExperiment(false);
    }
    
    //@Transactional
    public AssayPack insertExperiment(boolean isOpen) {
        ExperimentalAssay org = DomRepoTestBuilder.makeExperimentalAssay();
        
        EntityACL acl = new EntityACL();
        acl.setPublic(isOpen);
        acl.setOwner(fixtures.user1);
        acl.setCreator(fixtures.user1);
        acl.setSuperOwner(fixtures.user1.getSupervisor());
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = org.getId();
        info.entityType = EntityType.EXP_ASSAY;
        info.security = ExperimentHandler.convertACL(acl);
        
        AssayPack pack = expBoundles.newPack(org, info, acl);
        pack = expBoundles.save(pack);
        rdmSocialHandler.registerNewAssay(pack, fixtures.user1);
        //expBoundles.flush();
        return pack;
    }
    
    public List<JobSummary> getJobs() {

        String fName = "PPA_JOBS.xml";
        Path file = getResourceFile(fName);

        JobsContainer res = (JobsContainer) JAXB.unmarshal(file.toFile(), JobsContainer.class);
        return res.getSorted();
        
    }
    
    public JobSummary getJob() {
        return getJobs().get(0);
    }
    
    //@Transactional
    public void seedJustJob(JobSummary job, AssayPack exp) {
        //System.out.println("Seeding job "+job+":"+job.needsAttention());
        PPAJobSummary summary = PPAUtils.simplifyJob(job);
        //ppaRep.saveJobSummary(summary, exp);
        //ppaRep.saveJobFullDescription(job, exp);
        ppaRep.saveJobDetails(job,summary, exp);
    }
    
    //@Transactional
    public void seedFullJob(JobSummary job, AssayPack exp) {
        seedData(getData(),exp);
        seedJustJob(job,exp);
        seedJobArtifacts(job,exp);
    }
    
    //@Transactional
    public void seedData(List<DataTrace> data, AssayPack exp) {
        DataBundle rawData = new DataBundle();
        rawData.data.addAll(data);
        
        try {
            tsHandler.handleNewData(exp, rawData);
        } catch (DataProcessingException e) {
            throw new RuntimeException("Cannot seed data: "+e.getMessage(),e);
        }
    }

    //@Transactional
    protected void seedJobArtifacts(JobSummary job, AssayPack exp) {
        List<ResultsEntry> indResults = getJobIndResults(job);
        PPAJobSimpleResults simpleRes = getJobSimpleResults(job);
        PPAJobResultsGroups grouped = getJobResultsGroups(job);
        PPAJobSimpleStats simpleStats = getJobSimpleStats(job);
        StatsEntry fullStats = getJobFullStats(job);
        Map<Long,TimeSeries> fits = getFits(job);
        
        long jobId = job.getJobId();
        
        try {
        ppaRep.saveFits(fits, jobId, exp);
        ppaRep.saveJobIndResults(indResults, exp, jobId);
        ppaRep.saveJobSimpleResults(simpleRes, exp, jobId);
        ppaRep.saveJobResultsGroups(grouped, exp, jobId);
        ppaRep.saveJobSimpleStats(simpleStats, exp, jobId);
        ppaRep.saveJobFullStats(fullStats, exp, jobId);
        
        } catch (Exception e) {
            throw new RuntimeException("Cannot seed jobs artifacts: "+e.getMessage(),e);
        }
        
    }    
    
    
    public PPAJobSimpleStats getJobSimpleStats(JobSummary job) {
        Path file = getJobRelatedFile(job, "PPA_SIMPLE_STATS.json");
        return fromJSON(file,PPAJobSimpleStats.class);
    }
    
    public PPAJobSimpleResults getJobSimpleResults(JobSummary job) {
        Path file = getJobRelatedFile(job, "PPA_SIMPLE_RESULTS.json");
        return fromJSON(file,PPAJobSimpleResults.class);
    }    
    
    public StatsEntry getJobFullStats(JobSummary job) {
        Path file = getJobRelatedFile(job, "PPA_FULL_STATS.xml");
        StatsEntry stats = (StatsEntry) JAXB.unmarshal(file.toFile(), StatsEntry.class);
        return stats;
    }    
    
    public List<ResultsEntry> getJobIndResults(JobSummary job) {
        Path file = getJobRelatedFile(job, "PPA_RESULTS.xml");
        PPAJobIndResults container = (PPAJobIndResults) JAXB.unmarshal(file.toFile(), PPAJobIndResults.class);
        return container.results;
    }
    
    public PPAJobResultsGroups getJobResultsGroups(JobSummary job) {
        Path file = getJobRelatedFile(job, "GROUPED_RESULTS.json");
        return fromJSON(file,PPAJobResultsGroups.class);
    }
    
    public Map<Long, TimeSeries> getFits(JobSummary job) {
        Path file = getJobRelatedFile(job, "fit.ser");
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {

            Map<Long, TimeSeries> map = (Map<Long, TimeSeries>)in.readObject();
            return map;

        } catch (IOException|ClassNotFoundException e) {
            throw new RuntimeException("Cannot read fits: "+e.getMessage(),e);
        }        
    }
    
    public List<DataTrace> getData() {
        return getData("SHORT");        
    }
    
    public List<DataTrace> getData(String type) {
        switch(type) {
            case "SHORT": return getDataFromFile("LIN_DTR.ser");
            case "LARGE": return getDataFromFile("Large600x72LIN_DTR.ser");
            case "LONG": return getDataFromFile("Long10x2400LIN_DTR.ser");
            case "VERY_LONG": return getDataFromFile("VeryLong10x14400LIN_DTR.ser");
            default:
                throw new IllegalArgumentException("Unknown type of seed data: "+type);
        }
    }
    
    protected List<DataTrace> getDataFromFile(String fileName) {
        Path file = getResourceFile(fileName);
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {

            List<DataTrace> l = (List<DataTrace>)in.readObject();
            return l;

        } catch (IOException|ClassNotFoundException e) {
            throw new RuntimeException("Cannot read data: "+e.getMessage(),e);
        }        
    }    
    
    protected <T extends Object> T fromJSON(Path file,Class<T> valueType) {
        try {
            return mapper.readValue(file.toFile(), valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Path getJobRelatedFile(JobSummary job,String fName) {
        return getResourceFile(job.getJobId()+"."+fName);
    }
    
    protected Path getResourceFile(String name) {
        URL url = this.getClass().getResource(name);
        if (url == null) {
            throw new RuntimeException("Did not get file resource: "+name);
        }
        
        try {
            Path path = Paths.get(url.toURI());
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Cannot get file resource: "+name+": "+e.getMessage(),e);
        }
    }




    
    
}
