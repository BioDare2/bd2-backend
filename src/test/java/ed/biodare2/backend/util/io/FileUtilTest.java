/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.io;

import ed.biodare2.backend.util.io.FileUtil;
import java.io.IOException;
import static java.lang.Math.exp;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class FileUtilTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    FileUtil instance;
    
    @Before
    public void init() {
        instance = new FileUtil();
    }
    
    public FileUtilTest() {
    }

    @Test
    public void currentStampGivesCurrentDate() {
        
        String res = instance.currentStamp();
        
        Calendar cal = GregorianCalendar.getInstance();
        String exp = cal.get(GregorianCalendar.YEAR)+"_"+(cal.get(GregorianCalendar.MONTH)+1)+"_"+cal.get(GregorianCalendar.DAY_OF_MONTH);
        
        assertEquals(exp,res);
    }
    
    @Test
    public void uniqueFileGivesPathWIthGivenPrefixAndSuffix() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        String prefix = "ala";
        String suffix = ".xml";
        String exp = prefix+".1"+suffix;
        
        Path file = instance.uniqueFile(prefix, suffix, dir);
        assertEquals(exp,file.getFileName().toString());                
    }
    
    @Test
    public void uniqueFileIncrementsCounterWIthGivenPrefixAndSuffixToAvoidColisions() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        String prefix = "ala";
        String suffix = ".xml";
        
        Set<Path> paths = new HashSet<>();
        for (int i =0;i<5;i++) {
            paths.add(instance.uniqueFile(prefix, suffix, dir));
        }
        
        assertEquals(5,paths.size());
        for (Path path : paths) {
            assertTrue(path.getFileName().toString().startsWith(prefix));
            assertTrue(path.getFileName().toString().endsWith(suffix));
        }
            
    }
    
    @Test
    public void uniqueFileThrowsExceptionIfTooManyIterations() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        String prefix = "ala";
        String suffix = ".xml";

        try {
            for (int i =0;i<200;i++) {
                instance.uniqueFile(prefix, suffix, dir);
            }
            fail("Exception expected");
        } catch (IOException e) {}
        
    }    
    
    @Test
    public void backupMakesCopy() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        Path org = dir.resolve("cos.txt");
        Files.write(org, Arrays.asList("Blla bla bla"));
        
        Path cpy = instance.backup(org, dir);
        assertFalse(org.equals(cpy));
        assertTrue(Files.isRegularFile(cpy));
        assertEquals(Files.size(org),Files.size(cpy));
        
    }
    
    @Test
    public void removeDirectoryRemovesDirectoryAndItsContent() throws Exception {
        Path dir = testFolder.newFolder().toPath();
        
        Path subDir = dir.resolve("dir1");
        Files.createDirectory(subDir);
        
        Path f1 = dir.resolve("f1");
        Files.write(f1, Arrays.asList("Blla bla bla"));
        
        Path f2 = subDir.resolve("f2");
        Files.write(f2, Arrays.asList("Blla bla bla2"));

        assertTrue(Files.exists(f2));
        assertTrue(Files.exists(f1));
        assertTrue(Files.exists(dir));
        assertTrue(Files.exists(subDir));
        
        instance.removeRecursively(dir);
        assertFalse(Files.exists(f2));
        assertFalse(Files.exists(f1));
        assertFalse(Files.exists(subDir));
        assertFalse(Files.exists(dir));
        
    }
    
    @Test
    public void makeUploadSanitizesFilesNames() {
        
        
        List<String> names = Arrays.asList(
                "",
                null,
                "someąś ćóżń.xml",
                "bląświth<g>'a?s/w\\ell.txt\""
        );
        
        List<String> exps = Arrays.asList(
                "blank_name",
                "blank_name",
                "someas_cozn.xml",
                "blaswith_g__a_s_w_ell.txt_"
        );
        
        for (int i = 0;i<names.size();i++) {
            String resp = instance.sanitizeFileName(names.get(i));
            assertEquals(exps.get(i),resp);
        }
        
        
    }
    
}
