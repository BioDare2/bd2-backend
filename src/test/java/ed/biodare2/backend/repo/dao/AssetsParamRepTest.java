/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.assets.AssetParams;
import ed.biodare2.backend.repo.isa_dom.assets.AssetType;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class AssetsParamRepTest {

    @TempDir
    Path testFolder;
    
    public AssetsParamRepTest() {
    }

    ExperimentsStorage expStorage;
    ObjectMapper mapper;
    Path expDir;

    AssetsParamRep repo;
    
    @BeforeEach
    public void setUp() throws IOException {
        
        expStorage = mock(ExperimentsStorage.class);

	ObjectMapper mapper = JsonMapper.builder().build();
        
        repo = new AssetsParamRep(expStorage, mapper);
        
        expDir = testFolder.resolve("test");
	Files.createDirectories(expDir);
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);        
    }    
    
    @Test
    public void storeSavesParamsThatCanBeRetrieved() {
    
        long asset = 2;
        long version = 1;
        
        ExcelTSImportParameters params = DomRepoTestBuilder.makeExcelTSImportParameters();
        
        repo.storeParams(asset, version, params, expDir);
        
        Optional<ExcelTSImportParameters> res = repo.getParams(asset, version, expDir);
        assertTrue(res.isPresent());
        assertEquals(params.timeColumn,res.get().timeColumn);
    }
    
    @Test
    public void extractParamsReconstructsObject() {
        
        AssetParams params = new AssetParams(2,1);
        params.assetType = AssetType.NONE;
        params.paramsClass = Long.class.getName();
        params.params = "2";
        
        Long res = repo.<Long>extractParams(params).get();
        
        assertEquals(2L,(long)res);
    }    
    
    @Test
    public void canReadAndWriteParamsInfo() {
        
        AssetParams params = new AssetParams(2,1);
        params.assetType = AssetType.NONE;
        params.paramsClass = String.class.getName();
        params.params = "Costam";
        
        Path file = expDir.resolve("test.json");
        
        repo.saveParamsInfo(params, file);
        assertTrue(Files.exists(file));
        
        AssetParams cpy = repo.readParamsInfo(file);
        
        assertEquals(params.assetId,cpy.assetId);
        assertEquals(params.assetVersion,cpy.assetVersion);
        assertEquals(params.params,cpy.params);
    }
}
