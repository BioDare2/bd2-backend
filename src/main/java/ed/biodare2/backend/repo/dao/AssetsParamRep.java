/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.repo.isa_dom.assets.AssetParams;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class AssetsParamRep {
 
    final static String ASSETS_DIR = FileAssetRep.ASSETS_DIR;
    final static String PARAMS_DIR = "PARAMS";
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    final ObjectReader paramsReader;
    final ObjectWriter paramsWriter;    
    final ObjectMapper mapper;
    
    final ExperimentsStorage expStorage;
    
    final ResourceGuard<Long> guard = new ResourceGuard<>(50);
    
    
    @Autowired
    public AssetsParamRep(
            ExperimentsStorage expStorage,
            @Qualifier("PlainMapper") ObjectMapper mapper) {
        
        this.expStorage = expStorage;
        

        this.paramsReader = mapper.readerFor(AssetParams.class);
        this.paramsWriter = mapper.writerFor(AssetParams.class); 
        this.mapper = mapper;
    }
    
    public void storeParams(FileAsset assetDsc,Object params,AssayPack exp) {
        
        storeParams(assetDsc.id,assetDsc.last().versionId,params,exp);
    }
    
    protected void storeParams(long assetId,long versionId,Object params,AssayPack exp) {
        
        Path paramsDir = getParamsDir(exp.getId());
        storeParams(assetId,versionId,params,paramsDir);
    }
    
    protected void storeParams(long assetId,long versionId,Object parameters,Path paramsDir) {
    
        String fileName = assetIdsToName(assetId,versionId);
        Path file = paramsDir.resolve(fileName);
        
        
        AssetParams params = new AssetParams(assetId,versionId);
        params.paramsClass = parameters.getClass().getName();
        
        try {
            params.params = mapper.writeValueAsString(parameters);
        } catch (JsonProcessingException e) {
            throw new ServerSideException("Cannot save parameters for asset: "+assetId+"; "+e.getMessage(),e);
        }
        
        guard.guard(assetId, ()-> {
            if (Files.exists(file)) {
                log.info("Overwriting params file: "+file);
            }

            saveParamsInfo(params,file);
        });
        
        
    }
    
    public <T> Optional<T> getParams(FileAsset assetDsc,AssayPack exp) {
        return getParams(assetDsc.id,assetDsc.last().versionId,exp);
    }
    
    public <T> Optional<T> getParams(long assetId,long versionId,AssayPack exp) {
        Path paramsDir = getParamsDir(exp.getId());
        return getParams(assetId,versionId,paramsDir);
    }
    
    public <T> Optional<T> getParams(long assetId,long versionId,Path paramsDir) {

        String fileName = assetIdsToName(assetId,versionId);
        Path file = paramsDir.resolve(fileName);   
        
        if (!Files.isRegularFile(file))
            return Optional.empty();
        
        AssetParams params = guard.guard(assetId, (id) -> {
                return readParamsInfo(file);
        });
        
        if (params.assetId != assetId || params.assetVersion != versionId)
            throw new ServerSideException("Mismatch between ids number in params for asset: "+assetId+"; from: "+file);
        
        return extractParams(params);

    }
    
    protected <T> Optional<T> extractParams(AssetParams params) {
        try {
            Class cls = Class.forName(params.paramsClass);
        
            Object obj = mapper.readValue(params.params, cls);
        
            return Optional.of((T)obj);
        } catch (ClassNotFoundException | IOException e) {
            throw new ServerSideException("Cannot read parameters for "+params.assetId+"; "+e.getMessage(),e);
        }        
    }
    
    protected Path getParamsDir(long expId)  {
        try {
        Path p = expStorage.getExperimentDir(expId).resolve(ASSETS_DIR).resolve(PARAMS_DIR);
        if (!Files.isDirectory(p))
            Files.createDirectories(p);
        return p;
        } catch (IOException e) {
            throw new ServerSideException("Cannot access params dir: "+e.getMessage(),e);
        }
    }
    
    protected String assetIdsToName(long assetId, long versionId) {
        return "params."+assetId+"."+versionId+".json";
    }   
    
    protected void saveParamsInfo(AssetParams params,Path file) {
        try {
            paramsWriter.writeValue(file.toFile(), params);
        } catch (IOException e) {
            throw new ServerSideException("Cannot save params: "+e.getMessage(),e);
        }
    }
    
    protected AssetParams readParamsInfo(Path file) {
        try {
            return paramsReader.readValue(file.toFile());
        } catch (IOException e) {
            throw new ServerSideException("Cannot read params info: "+e.getMessage(),e);
        }    
    }


    

    
}
