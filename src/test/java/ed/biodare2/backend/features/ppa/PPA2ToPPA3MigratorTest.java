/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.testutil.PPATestSeederJC2;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class PPA2ToPPA3MigratorTest {
    
    public PPA2ToPPA3MigratorTest() {
    }
    
    ObjectMapper mapper = PPATestSeederJC2.makeMapper();
    PPAArtifactsRep ppa2Rep;
    PPAArtifactsRepJC2 ppa3RepJC2;   
    
    PPA2ToPPA3Migrator instance;
    
    @Before
    public void setUp() {
        
     ppa2Rep = mock(PPAArtifactsRep.class);
     ppa3RepJC2 = mock(PPAArtifactsRepJC2.class);           
     
     instance = new PPA2ToPPA3Migrator(ppa2Rep, ppa3RepJC2);
    }

    @Test
    public void toNewStateWorks() {
        
        assertEquals(State.FINISHED, instance.toNewState(ed.robust.jobcenter.dom.state.State.FINISHED));
        assertEquals(State.SUCCESS, instance.toNewState(ed.robust.jobcenter.dom.state.State.SUCCESS));
        assertEquals(State.SUBMITTED, instance.toNewState(ed.robust.jobcenter.dom.state.State.SUBMITTED));
    }
    
    @Test
    public void toUUIDWorks() {
        String id = instance.toUUID(1000, 123).toString();
        UUID uuid = UUID.fromString(id);
        assertNotNull(uuid);
        
    }
    
    @Test
    public void toLocalDateWorks() {
        
        LocalDateTime org = LocalDateTime.now();
        long time = org.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Date date = new Date(time);
        
        LocalDateTime cpy = instance.toLocalDate(date);
        org  = org.truncatedTo(ChronoUnit.SECONDS);
        cpy = cpy.truncatedTo(ChronoUnit.SECONDS);
        
        assertEquals(org, cpy);
    }
    
    @Test
    public void convertsJobSummary() throws IOException {
        Path file = getFileLocation("old_PPA_JOB_SUMMARY.json");
        
        PPAJobSummary oldJob = mapper.readValue(file.toFile(), PPAJobSummary.class);
        
        ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary upgraded = instance.upgradeJob(oldJob, 123);
        
        assertNotNull(upgraded.jobId);
        assertEquals(oldJob.jobId, upgraded.oldId);
        assertEquals(123, upgraded.parentId);
        assertEquals(oldJob.summary, upgraded.summary);
        assertEquals(LocalDate.of(2020, 3, 18), upgraded.submitted.toLocalDate());
        
    }
    
    Path getFileLocation(String fName)  {
        try {
            return new File(this.getClass().getResource(fName).toURI()).toPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }    
    
}
