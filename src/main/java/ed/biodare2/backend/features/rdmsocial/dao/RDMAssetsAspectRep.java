/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.features.rdmsocial.RDMAssetsAspect;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Zielu
 */
@Repository
@CacheConfig(cacheNames = {"RDMAssetsAspect"})
public class RDMAssetsAspectRep {
   
    final static String RDM_GUI_DIR = "RDM";
    final static String RDM_GUI_FILE = "rdm_asset_aspect.json";
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    final ObjectReader aspectReader;
    final ObjectWriter aspectWriter;  
    final ExperimentsStorage expStorage;
    final ResourceGuard<Long> resourceGuard = new ResourceGuard<>(100);
    
    @Autowired
    public RDMAssetsAspectRep(ExperimentsStorage expStorage,@Qualifier("DomMapper") ObjectMapper mapper) {
        
        this.expStorage = expStorage;
        

        this.aspectReader = mapper.readerFor(RDMAssetsAspect.class);
        this.aspectWriter = mapper.writerFor(RDMAssetsAspect.class);        
        
    }
    
    @Cacheable(key="{#parentId,#type}",unless="#result == null")
    public Optional<RDMAssetsAspect> findByParent(long parentId,EntityType type)  {
        
        Path storageDir = getStorageDir(parentId, type);
        
        return readAspect(parentId,storageDir);
    }
    
    @CachePut(key="{#aspect.parentId,#aspect.entityType}")
    @Transactional
    public RDMAssetsAspect save(RDMAssetsAspect aspect) throws ServerSideException {
       return resourceGuard.guard(aspect.parentId, (id) -> {
            try {

                Path storageDir = getStorageDir(aspect.parentId, aspect.entityType);
                Path file = getAspectFile(storageDir);

                /*if (Files.exists(systemFile))
                    fileUtil.backup(systemFile,systemDir);
                */
                aspectWriter.writeValue(file.toFile(), aspect);
                return aspect;
            } catch (IOException e) {
                throw new ServerSideException("Cannot access system info: "+e.getMessage(),e);
            }
       });
    }

    protected Path getStorageDir(long parentId, EntityType entityType) {
        
        try {
            
        Path dir = expStorage.getExperimentDir(parentId).resolve(RDM_GUI_DIR);
        if (!Files.exists(dir))
            Files.createDirectories(dir);
        
        return dir;
        } catch (IOException  e) {
            throw new ServerSideException("Cannot get sytem dir: "+e.getMessage(),e);
        }    
    }

    protected Path getAspectFile(Path storageDir) {
        return storageDir.resolve(RDM_GUI_FILE);
    }

    protected Optional<RDMAssetsAspect> readAspect(long parentId,Path storageDir) {
        
        
        
       return resourceGuard.guard(parentId, (id) -> {
            try {

                Path file = getAspectFile(storageDir);
                if (!Files.exists(file))
                    return Optional.<RDMAssetsAspect>empty();
                
                RDMAssetsAspect asp = aspectReader.readValue(file.toFile());
                return Optional.of(asp);
                
            } catch (IOException e) {
                throw new ServerSideException("Cannot access system info: "+e.getMessage(),e);
            }
       });
        
    }
    
    
}
