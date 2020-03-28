/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.tsdata.datahandling.DataProcessingException;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.jobcenter.JobsContainer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.bind.JAXB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    TSDataHandler tsHandler;
    
    
    @Autowired
    public ObjectMapper mapper;
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public AssayPack insertExperiment() {
        return insertExperiment(false);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
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
