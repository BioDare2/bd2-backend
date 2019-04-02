/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.EnvironmentVariables;
import ed.biodare2.MockEnvironmentVariables;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.web.rest.DeniedAccessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tzielins
 */
public class FileUploadHandlerTest {
    
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    Path bdStorageDir;
    Path uploadsDir;
    FileUploadHandler handler;
    ObjectMapper mapper;
    
    EnvironmentVariables environment;
    
    public FileUploadHandlerTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        bdStorageDir = testFolder.newFolder().toPath();
        uploadsDir = bdStorageDir.resolve(FileUploadHandler.UPLOADS_STORAGE_DIR);
        
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.storageDir = bdStorageDir.toString();
        environment = var.mock(); //new EnvironmentVariables(bdStorageDir.toString(),,,"","","","","");
        handler = new FileUploadHandler(environment,mapper);
        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void uponConstructionCanInitializeFolders() {
        
        Path dir = bdStorageDir.resolve("xxx");
        assertFalse(Files.exists(dir));
        
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.storageDir = dir.toString();
        EnvironmentVariables env = var.mock(); //new EnvironmentVariables(dir.toString(),"http://localhost","http://localhost:8084/JobCenter/PPAJobCenterWS?wsdl","","","","","");
        
        handler = new FileUploadHandler(env,mapper);

        assertTrue(Files.exists(dir.resolve(FileUploadHandler.UPLOADS_STORAGE_DIR)));
        
    }    
    
    @Test
    public void getTempFileGivesSensibleFileNameInUploadsDir() throws IOException {
        
        Path file = handler.getTmpFile();
        assertEquals(uploadsDir,file.getParent());
        
        String name = file.getFileName().toString();
        assertTrue(name.startsWith("_upload"));
        assertTrue(name.endsWith(".tmp"));
        assertFalse(name.contains("&"));
        assertFalse(name.contains("#"));
        
    }
    
    @Test
    public void getInfoFileGivesFileInUploadsWithIdJsonName() {
        String id = "1234";
        Path exp = uploadsDir.resolve("1234.json");
        
        Path res = handler.getInfoFile(id);
        assertEquals(exp,res);
    }
    
    @Test 
    public void saveFileInfoSaveFileToUploads() throws IOException {
        
        UploadFileInfo info = new UploadFileInfo();
        info.id = "123";
        
        Path file = handler.getInfoFile(info.id);
        assertFalse(Files.exists(file));
        
        handler.saveFileInfo(info);
        assertTrue(Files.exists(file));
    }
    
    @Test
    public void makeUploadInfoCopiesTheCorrectDetails() {
        
        MultipartFile uploaded = new MockMultipartFile("cos", "original", "text", new byte[0]) ;
        Path tmp = uploadsDir.resolve("tmp");
        UserAccount user = new UserAccount();
        user.setLogin("tomek");
        
        UploadFileInfo info = handler.makeUploadInfo(uploaded, tmp, user);
        assertEquals(tmp.getFileName().toString(),info.tmpFileName);
        assertEquals("original",info.originalFileName);
        assertEquals("text",info.contentType);
        assertEquals("tomek",info.uploadedBy);
        assertEquals(LocalDate.now(),LocalDate.from(info.uploadedOn));
        
    }
    
    @Test
    public void makeUploadSanitizesFilesNames() {
        
        String t = Normalizer.normalize("someąśćłóżń./?.xml", Normalizer.Form.NFKD);
        String s = t.replaceAll("\\p{InCombiningDiacriticalMarks}+","");
        String c = s.replaceAll("[^a-zA-Z_0-9.]","_");
        
        System.out.println(t+" : "+s+" : "+c);
        Path tmp = uploadsDir.resolve("tmp");
        UserAccount user = new UserAccount();
        user.setLogin("tomek");
        
        List<String> names = Arrays.asList(
                "someąśćóżń.xml",
                "bląświth<g>'a?s/w\\ell.txt\""
        );
        
        List<String> exps = Arrays.asList(
                "someascozn.xml",
                "blaswith_g__a_s_w_ell.txt_"
        );
        
        for (int i = 0;i<names.size();i++) {
            MultipartFile uploaded = new MockMultipartFile("cos", names.get(i), "text", new byte[0]);
            UploadFileInfo info = handler.makeUploadInfo(uploaded, tmp, user);
            assertEquals(exps.get(i),info.originalFileName);
        }
        
        
    }
    
    @Test
    public void saveSavesContentAndItsDescriptionToUploads() throws IOException {
        MultipartFile uploaded = new MockMultipartFile("cos", "original", "text", new byte[10]);
        UserAccount user = new UserAccount();
        user.setLogin("tomek");
        
        assertEquals(0,Files.list(uploadsDir).count());
        
        handler.save(uploaded, user);
        
        assertEquals(2,Files.list(uploadsDir).count());
        
        Path content = Files.list(uploadsDir).filter( f -> f.toString().endsWith(".tmp")).findFirst().get();
        assertEquals(10,Files.size(content));
        
        Path description = Files.list(uploadsDir).filter( f -> f.toString().endsWith(".json")).findFirst().get();
        assertTrue(Files.isRegularFile(description));
        
    }
      
    @Test
    public void getInfoGetsCorrectInfo() throws IOException {
        MultipartFile uploaded = new MockMultipartFile("cos", "original", "text", new byte[10]);
        UserAccount user = new UserAccount();
        user.setLogin("tomek");
        
        assertEquals(0,Files.list(uploadsDir).count());
        
        UploadFileInfo info = handler.save(uploaded, user);
        
        UploadFileInfo res = handler.getInfo(info.id);
        
        assertEquals(info,res);
        
    }
    
    @Test
    public void getThrowsDeniedIfUserMismatch() throws IOException {
        MultipartFile uploaded = new MockMultipartFile("cos", "original", "text", new byte[10]);
        UserAccount user = new UserAccount();
        user.setLogin("tomek");
        
        assertEquals(0,Files.list(uploadsDir).count());
        
        UploadFileInfo info = handler.save(uploaded, user);
        
        try {
            user.setLogin("other");
            Path file = handler.get(info.id,user);
            fail("Exception expected");
        } catch (DeniedAccessException e) {};
    
    }

    @Test
    public void getGivesPathToSavedUpload() throws IOException {
        MultipartFile uploaded = new MockMultipartFile("cos", "original", "text", new byte[10]);
        UserAccount user = new UserAccount();
        user.setLogin("tomek");
        
        assertEquals(0,Files.list(uploadsDir).count());
        
        UploadFileInfo info = handler.save(uploaded, user);
        
        Path file = handler.get(info.id,user);
        assertTrue(Files.exists(file));
        assertEquals(10,Files.size(file));
    
    }
    
    @Test
    public void cleanUpLeavesUnprefixedFilesIntacts() throws IOException {
        handler.timeToLive = 0;
        
        Path f1 = uploadsDir.resolve("file1.json");
        Path f2 = uploadsDir.resolve("file2.tmp");
        
        Files.createFile(f1);
        Files.createFile(f2);
        
        Files.setLastModifiedTime(f1, FileTime.from(Instant.now().minus(2,ChronoUnit.HOURS)));
        Files.setLastModifiedTime(f2, FileTime.from(Instant.now().minus(2,ChronoUnit.HOURS)));
        
        assertTrue(Files.exists(f1));
        assertTrue(Files.exists(f2));
        
        handler.cleanUP();
        
        assertTrue(Files.exists(f1));
        assertTrue(Files.exists(f2));
        
    }
    
    @Test
    public void cleanUpRemovesExpiredTmpFiles() throws IOException {
        handler.timeToLive = 1;
        
        Path f1 = uploadsDir.resolve(handler.T_PREFIX+".json");
        Path f2 = uploadsDir.resolve(handler.T_PREFIX+".tmp");
        
        Files.createFile(f1);
        Files.createFile(f2);
        
        Files.setLastModifiedTime(f1, FileTime.from(Instant.now().minus(2,ChronoUnit.HOURS)));
        Files.setLastModifiedTime(f2, FileTime.from(Instant.now().minus(2,ChronoUnit.HOURS)));
        
        assertTrue(Files.exists(f1));
        assertTrue(Files.exists(f2));
        
        handler.cleanUP();
        
        assertFalse(Files.exists(f1));
        assertFalse(Files.exists(f2));
        
    }    
    
    @Test
    public void cleanUpRemovesOnlyExpiredTmpFiles() throws IOException {
        handler.timeToLive = 1;
        
        Path f1 = uploadsDir.resolve(handler.T_PREFIX+".json");
        Path f2 = uploadsDir.resolve(handler.T_PREFIX+".tmp");
        
        Files.createFile(f1);
        Files.createFile(f2);
        
        Files.setLastModifiedTime(f1, FileTime.from(Instant.now().minus(2,ChronoUnit.HOURS)));
        Files.setLastModifiedTime(f2, FileTime.from(Instant.now().minus(30,ChronoUnit.MINUTES)));
        
        assertTrue(Files.exists(f1));
        assertTrue(Files.exists(f2));
        
        handler.cleanUP();
        
        assertFalse(Files.exists(f1));
        assertTrue(Files.exists(f2));
        
    }     
    

}
