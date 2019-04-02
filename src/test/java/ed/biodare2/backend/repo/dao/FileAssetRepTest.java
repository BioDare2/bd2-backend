/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.repo.dao.FileAssetRep;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.handlers.ExperimentDataHandler;
import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.handlers.UploadFileInfo;
import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.repo.isa_dom.assets.AssetType;
import ed.biodare2.backend.repo.isa_dom.assets.AssetVersion;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.isa_dom.assets.FileAssets;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class FileAssetRepTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public FileAssetRepTest() {
    }
    
    ExperimentsStorage expStorage;
    IdGenerator idGenerator;
    FileUploadHandler uploads;
    FileAssetRep assets;
    ObjectMapper mapper;
    
    @Before
    public void setUp() throws IOException {
        
        expStorage = mock(ExperimentsStorage.class);
        idGenerator = mock(IdGenerator.class);
        when(idGenerator.next()).thenReturn(2L);
        uploads = mock(FileUploadHandler.class);
        
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        assets = new FileAssetRep(idGenerator,expStorage, uploads,mapper);
        
        
    }    

    @Test
    public void makeUniqueNameCreatesUniqueName() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        String fName = "up.txt";
        
        Path exs = dir.resolve(fName);
        Files.createFile(exs);
        
        Path unique = assets.makeUniqueName(dir, fName);
        
        assertEquals(dir,unique.getParent());
        assertFalse(exs.equals(unique));
        assertFalse(Files.exists(unique));
        assertTrue(unique.getFileName().toString().endsWith(fName));
    }
    
    
    @Test
    public void savesAndReadsAssetsInfo() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        
        FileAssets info = new FileAssets();
        FileAsset asset = new FileAsset(1,"jakis.xml");
        
        asset.add("_local.xml","original", "txt");        
        info.set(asset);
        
        assets.saveAssetsInfo(info, dir);
        
        FileAssets cpy = assets.getAssetsInfo(dir);
        assertNotNull(cpy);
        
        FileAsset cpyF = cpy.get(asset.id).orElse(null);
        assertNotNull(cpyF);
        
        assertEquals("jakis.xml",cpyF.originalName);
        assertEquals("_local.xml", cpyF.getLocalFile());
        assertEquals("txt", cpyF.getContentType());
        
    }

    
    
    @Test
    public void storeFileUploadSaveTheFileAndItsDescription() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();        
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        Path upDir = testFolder.newFolder().toPath();
        Path upFile = upDir.resolve("file.txt");
        Files.write(upFile, Arrays.asList("Cos tam"));
        
        String fileId = "1";
        UploadFileInfo upload = new UploadFileInfo();
        upload.id = fileId;
        upload.contentType = "text";
        upload.originalFileName = "file2.txt";
        
        when(uploads.get(eq(fileId), anyObject())).thenReturn(upFile);
        when(uploads.getInfo(eq(fileId))).thenReturn(upload);
        
        AssayPack exp = new MockExperimentPack(2);
        
        UserAccount user = new UserAccount();
        
        assets.storeFileUpload(fileId,"file2.txt", AssetType.TS_DATA, exp, user);
        
        assertTrue(Files.isDirectory(expDir.resolve("ASSETS")));
        assertTrue(Files.isDirectory(expDir.resolve("ASSETS").resolve("FILES")));
        assertTrue(Files.isRegularFile(expDir.resolve("ASSETS").resolve("FILES_INFO.json")));
        
        assertEquals(1,Files.list(expDir.resolve("ASSETS").resolve("FILES")).count());
        assertEquals(Files.size(upFile),Files.size(Files.list(expDir.resolve("ASSETS").resolve("FILES")).findFirst().get()));
        
        List<String> files = assets.getAssets(exp).map( asset -> asset.originalName).collect(Collectors.toList());
        assertEquals(Arrays.asList(upload.originalFileName),files);
    }
    
    @Test
    public void storeCallsGeneratorOnlyForNewNames() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();        
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        Path upDir = testFolder.newFolder().toPath();
        Path upFile = upDir.resolve("file.txt");
        Files.write(upFile, Arrays.asList("Cos tam"));
        
        String fileId = "1";
        UploadFileInfo upload = new UploadFileInfo();
        upload.id = fileId;
        upload.contentType = "text";
        upload.originalFileName = "file3.txt";
        String assetName = upload.originalFileName;
        
        when(uploads.get(eq(fileId), anyObject())).thenReturn(upFile);
        when(uploads.getInfo(eq(fileId))).thenReturn(upload);
        
        AssayPack exp = new MockExperimentPack(2);
        
        UserAccount user = new UserAccount();
        
        assets.storeFileUpload(fileId,assetName, AssetType.TS_DATA, exp, user);
        
        verify(idGenerator, times(1)).next();
        
        assets.storeFileUpload(fileId,assetName, AssetType.TS_DATA, exp, user);
        verify(idGenerator, times(1)).next();
        
        
        assertEquals(2,Files.list(expDir.resolve("ASSETS").resolve("FILES")).count());
    }    
    
    @Test
    public void storeFileUploadsSaveTheFilesAndItsDescription() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();        
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        when(idGenerator.next())
                .thenReturn(2L)
                .thenReturn(3L)
                ;
        
        Path upDir = testFolder.newFolder().toPath();
        Path upFile1 = upDir.resolve("file1.txt");
        Files.write(upFile1, Arrays.asList("Cos tam"));
        
        String fileId1 = "1";
        UploadFileInfo upload1 = new UploadFileInfo();
        upload1.id = fileId1;
        upload1.contentType = "text";
        upload1.originalFileName = "f1.txt";
        
        when(uploads.get(eq(fileId1), anyObject())).thenReturn(upFile1);
        when(uploads.getInfo(eq(fileId1))).thenReturn(upload1);
        
        Path upFile2 = upDir.resolve("file2.txt");
        Files.write(upFile2, Arrays.asList("Cos tam2"));
        
        String fileId2 = "2";
        UploadFileInfo upload2 = new UploadFileInfo();
        upload2.id = fileId2;
        upload2.contentType = "text";
        upload2.originalFileName = "f2.txt";
        
        when(uploads.get(eq(fileId2), anyObject())).thenReturn(upFile2);
        when(uploads.getInfo(eq(fileId2))).thenReturn(upload2);        
        
        List<UploadFileInfo> files = Arrays.asList(upload1,upload2);
        
        AssayPack exp = new MockExperimentPack(2);
        
        UserAccount user = new UserAccount();
        
        List<FileAsset> ass = assets.storeFileUploads(files, AssetType.FILE, exp, user);
        
        assertTrue(Files.isDirectory(expDir.resolve("ASSETS")));
        assertTrue(Files.isDirectory(expDir.resolve("ASSETS").resolve("FILES")));
        assertTrue(Files.isRegularFile(expDir.resolve("ASSETS").resolve("FILES_INFO.json")));
        
        assertEquals(2,Files.list(expDir.resolve("ASSETS").resolve("FILES")).count());
        assertEquals(2,ass.size());
        System.out.println(ass);
        assertEquals(2,assets.getAssets(exp).count());
        
        List<String> names = assets.getAssets(exp).map( asset -> asset.assetName).collect(Collectors.toList());
        assertEquals(Arrays.asList(upload1.originalFileName,upload2.originalFileName),names);
        
        names = assets.getAssets(exp).map( asset -> asset.originalName).collect(Collectors.toList());
        assertEquals(Arrays.asList(upload1.originalFileName,upload2.originalFileName),names);
    }
    
    
    
    @Test
    public void lastIdGivesLastId() throws Exception {
        
        Path exps = testFolder.newFolder().toPath();        
        Path expDir1 = exps.resolve("1");        
        Path expDir2 = exps.resolve("2"); 
        when(expStorage.getExperimentsDir()).thenReturn(exps);
        when(expStorage.getExperimentDir(eq(1L))).thenReturn(expDir1);
        when(expStorage.getExperimentDir(eq(2L))).thenReturn(expDir2);
        
        Path upDir = testFolder.newFolder().toPath();
        Path upFile = upDir.resolve("file.txt");
        Files.write(upFile, Arrays.asList("Cos tam"));
        
        String fileId = "1";
        UploadFileInfo upload = new UploadFileInfo();
        upload.id = fileId;
        upload.contentType = "text";
        upload.originalFileName = "file3.txt";
        String assetName = upload.originalFileName;

        when(uploads.get(eq(fileId), anyObject())).thenReturn(upFile);
        when(uploads.getInfo(eq(fileId))).thenReturn(upload);
        
        AssayPack exp = new MockExperimentPack(1);
        when(idGenerator.next()).thenReturn(10L);
        
        UserAccount user = new UserAccount();        
        assets.storeFileUpload(fileId,assetName, AssetType.TS_DATA, exp, user);
        
        when(idGenerator.next()).thenReturn(15L);
        upload.originalFileName = "file4.txt";
        assetName = upload.originalFileName;
        
        assets.storeFileUpload(fileId,assetName, AssetType.TS_DATA, exp, user);

        when(idGenerator.next()).thenReturn(12L);
        exp = new MockExperimentPack(2);

        assets.storeFileUpload(fileId,assetName, AssetType.TS_DATA, exp, user);

        verify(idGenerator, times(3)).next();
        long last = assets.lastId();
        
        assertEquals(15,last);
    }    
    

    @Test
    public void getAssetsHandlesNewExp()  throws Exception {
        Path expDir = testFolder.newFolder().toPath();        
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
 
        AssayPack exp = new MockExperimentPack(2);

        Stream<FileAsset> files = assets.getAssets(exp);
        assertEquals(0,files.count());
    }
    
    @Test
    public void getAssetsReturnsExistingAssetsStream()  throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();        
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
 
        AssayPack exp = new MockExperimentPack(2);  
        
        Path assetsDir = assets.getAssetsDir(exp.getId());
        
        FileAssets info = new FileAssets();
        FileAsset asset = new FileAsset(1,"jakis.xml");
        
        asset.add("_local.xml","original", "txt");             
        info.set(asset);
        
        assets.saveAssetsInfo(info, assetsDir);
        
        List<String> names = assets.getAssets(exp).map(f -> f.originalName).collect(Collectors.toList());
        assertEquals(Arrays.asList("jakis.xml"),names);
    }
    
    
    @Test
    public void fixesFileAssets() throws IOException {
        
        FileAssets bad = mapper.readValue(this.getClass().getResourceAsStream("BAD_FILES_INFO.json"), FileAssets.class);
        assertNotNull(bad);
        
        FileAssets fixed = assets.fixAssets(bad, ExperimentDataHandler.TSAssetName);
        assertNotNull(fixed);
        
        assertEquals(3,fixed.stream().count());
        
        FileAsset f = fixed.findByNameAndType(ExperimentDataHandler.TSAssetName, AssetType.TS_DATA).get();
        assertEquals(10113,f.id);
        assertEquals("BD2-LD2DD-exp_12730498782920_16-11-29_12730498782920_data.xlsx",f.originalName);
        assertEquals(2,f.versions.size());
        
        AssetVersion ver = f.last();
        assertEquals(2,ver.versionId);
        assertEquals("1_BD2-LD2DD-exp_12730498782920_16-11-29_12730498782920_data.xlsx",ver.localFile);
        
        f = fixed.findByNameAndType("10050.LIN_DTR.data (1).csv", AssetType.FILE).get();
        assertEquals(10103,f.id);
        assertEquals("10050.LIN_DTR.data (1).csv",f.originalName);
        assertEquals(1,f.versions.size());
        
        ver = f.last();
        assertEquals(1,ver.versionId);
        assertEquals("1_10050.LIN_DTR.data (1).csv",ver.localFile);
    }
    
    @Test
    public void saveMakesBackups() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        assertEquals(0L,Files.list(expDir).count());
        
        FileAssets info = new FileAssets();
        Path assetsDir = expDir.resolve(FileAssetRep.ASSETS_DIR);
        Files.createDirectories(assetsDir);
        
        assets.saveAssetsInfo(info, assetsDir);
        
        assertEquals(1L,Files.list(assetsDir).count());
        
        assets.saveAssetsInfo(info, assetsDir);
        assertEquals(2L,Files.list(assetsDir).count());
        
    } 
    
    /*
    
    @Test
    public void canSaveAndReadsAssetsInfo() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        
        FileAssets info = new FileAssets();
        FileAsset asset = new FileAsset(1,"jakis.xml");
        
        ExcelTSImportParameters params = new ExcelTSImportParameters();
        params.timeColumn = DomRepoTestBuilder.makeTimeColumn();
        
        asset.add("bla.xml", "txt", params);        
        info.tsFiles.put(asset.originalName, asset);
        
        assets.saveAssetsInfo(info, dir);
        
        FileAssets cpy = assets.getAssetsInfo(dir);
        assertNotNull(cpy);
        
        FileAsset cpyF = cpy.tsFiles.get(asset.originalName);
        assertNotNull(cpyF);
        
        assertEquals("bla.xml", cpyF.getLocalFile());
        assertEquals("txt", cpyF.getContentType());
        
        assertEquals(params.timeColumn,((ExcelTSImportParameters)cpyF.versions.get(0).importParams).timeColumn);
    }*/
    
    /*
    @Test
    public void storeTSUploadStoresTheFileAndItsDescription() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();        
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        Path upDir = testFolder.newFolder().toPath();
        Path upFile = upDir.resolve("file.txt");
        Files.write(upFile, Arrays.asList("Cos tam"));
        
        UploadFileInfo upload = new UploadFileInfo();
        upload.contentType = "text";
        upload.originalFileName = "file2.txt";
        
        when(uploads.get(anyString(), anyObject())).thenReturn(upFile);
        when(uploads.getInfo(anyString())).thenReturn(upload);
        
        String fileId = "1";
        TSImportParameters importParameters = new ExcelTSImportParameters();
        AssayPack exp = new MockExperimentPack(2);
        
        UserAccount user = new UserAccount();
        
        assets.storeTSUpload(fileId, importParameters, exp, user, true);
        
        assertTrue(Files.isDirectory(expDir.resolve("ASSETS")));
        assertTrue(Files.isDirectory(expDir.resolve("ASSETS").resolve("FILES")));
        assertTrue(Files.isRegularFile(expDir.resolve("ASSETS").resolve("FILES_INFO.json")));
        
        assertEquals(1,Files.list(expDir.resolve("ASSETS").resolve("FILES")).count());
        assertEquals(Files.size(upFile),Files.size(Files.list(expDir.resolve("ASSETS").resolve("FILES")).findFirst().get()));
        
    }
    */
}
