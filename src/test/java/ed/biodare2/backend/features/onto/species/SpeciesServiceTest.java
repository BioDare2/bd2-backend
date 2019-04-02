/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.onto.species;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class SpeciesServiceTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    Path configFile;
    SpeciesService service;
    
    public SpeciesServiceTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        
        configFile = testFolder.newFile().toPath();
        service = new SpeciesService(configFile.toString());
    }

    @Test
    public void testSetupWork() {
        assertNotNull(service);
    }
    
    @Test
    public void findAllReturnsNotBackedValue() {
        
        List<String> species = service.findAll();
        species.add("new one");
        
        List<String> known = service.findAll();
        assertEquals(known, service.findAll());
        assertNotEquals(known, species);
        
    }
    
    @Test
    public void readSpeciesReadsListFromFileStripingSpacesAndOrdering() throws Exception {
        
        Path tFile = testFolder.newFile().toPath();
        
        List<String> content = Arrays.asList("Species 1 ","Species 2",""," Species 3","A","  ");
        Files.write(tFile, content);
        
        List<String> expected = Arrays.asList("A","Species 1","Species 2", "Species 3");
        
        assertEquals(expected, service.readSpecies(tFile));
    }
    
    @Test
    public void updateReplaceKnownWithContentOfConfigFile() throws Exception {
        
        List<String> exp = Collections.emptyList();
        assertEquals(exp, service.findAll());
        
        List<String> content = Arrays.asList("Species 1","Species 2");
        Files.write(configFile, content);
        
        service.updateKnown();
        assertEquals(content, service.findAll());
        
        content = Arrays.asList("Species 3");
        Files.write(configFile, content);        
        service.updateKnown();
        assertEquals(content, service.findAll());
    }
    
}
