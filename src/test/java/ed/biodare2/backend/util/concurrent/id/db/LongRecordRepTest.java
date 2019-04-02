/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id.db;

//import ed.biodare2.backend.SimpleTestConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class LongRecordRepTest {
    
    @Autowired
    LongRecordRep repository;

    public LongRecordRepTest() {
    }
    
    List<LongRecord> created = new ArrayList<>();
    
    
    
    @Before
    //@Transactional
    public void setUp() {
        init(repository,created);
    }
    
    @After
    //@Transactional
    public void tearDown() {
        clear(repository,created);
    }
    
    
    @Test
    public void testSetup() {
        
        List<LongRecord> all = repository.findAll();
        
        assertNotNull(all);
        assertFalse(all.isEmpty());
        
        LongRecord rec = new LongRecord("xx2",10);
        
        rec = repository.save(rec);
        created.add(rec);
        
        LongRecord res = repository.findById(rec.getRecordName()).get();
        assertNotNull(res);
        
    }
    
    public static void init(LongRecordRep repository,List<LongRecord> created) {
        
        LongRecord rec = new LongRecord("rec1",1);        
        created.add(repository.save(rec));
        
        rec = new LongRecord("rec2",5);        
        created.add(repository.save(rec));
        
    }

    public static void clear(LongRecordRep repository,List<LongRecord> created) {
        
        created.forEach(repository::delete);
        
        created.clear();
    }
    
    
}
