/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextDataTableViewTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public TextDataTableViewTest() {
    }
    
    TextDataTableView instance;
    
    @Before
    public void setUp() {
        instance = new TextDataTableView();
    }

    @Test
    public void testCountPresence() throws Exception {
        String sep = ",";
        String line = "";
        
        assertEquals(0, instance.countPresence(line, sep));
        
        line = "abderfgghij";
        assertEquals(0, instance.countPresence(line, sep));
        
        line = ",abderfgghij";
        assertEquals(1, instance.countPresence(line, sep));
        
        line = "abderfgghij,";
        assertEquals(1, instance.countPresence(line, sep));
        
        line = ",,abderfg,g,h,,ij,";
        assertEquals(7, instance.countPresence(line, sep));
        
    }
    
    @Test
    public void testIsSuitableFormat() throws Exception {
        
        Path file = testFolder.newFile().toPath();
        assertFalse(instance.isSuitableFormat(file, ","));
        
        List<String> lines = List.of("alkafaf","ma","kota","kot ma ale");        
        Files.write(file, lines);        
        assertFalse(instance.isSuitableFormat(file, ","));
        
        file = testFolder.newFile().toPath();
        lines = List.of("alk,afa,f","ma","ko,ta","kot ma ale");       
        Files.write(file, lines);        
        assertFalse(instance.isSuitableFormat(file, ","));        
        
        file = testFolder.newFile().toPath();
        lines = List.of("alk,afa,f","ma","ko,t,a","k,ot ,ma ale");       
        Files.write(file, lines);        
        assertTrue(instance.isSuitableFormat(file, ","));        
        
    }
    
}
