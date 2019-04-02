/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.util.io.FileUtil;
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
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
@CacheConfig(cacheNames = {"SystemInfo"})
public class SystemInfoRep {
    
    
    final static String SYSTEM_DIR = "SYSTEM";
    final static String SYSTEM_SUFFIX = ".sys.json";
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    final ObjectReader infoReader;
    final ObjectWriter infoWriter;    
    final ExperimentsStorage expStorage;
    final ResourceGuard<Long> resourceGuard = new ResourceGuard<>(100);
    final FileUtil fileUtil = new FileUtil();
    
    //final XMLUtil xmlUtil = new XMLUtil();
    
    
    
    @Autowired
    public SystemInfoRep(ExperimentsStorage expStorage,@Qualifier("DomMapper") ObjectMapper mapper) {
        this.expStorage = expStorage;
        

        this.infoReader = mapper.readerFor(SystemInfo.class);
        this.infoWriter = mapper.writerFor(SystemInfo.class);        
    }
    
  

    @Cacheable(key="{#parentId,#type}",unless="#result == null")
    public Optional<SystemInfo> findByParent(long parentId,EntityType type)  {
        
        Path systemDir = getSystemDir(parentId, type);
        
        switch (type) {
            case EXP_ASSAY: return findSysInfo(parentId,systemDir);
            default: throw new IllegalArgumentException("Unsupported type: "+type);
        }
    }
    
    @CachePut(key="{#info.parentId,#info.entityType}")
    public SystemInfo save(SystemInfo info) throws ServerSideException {
       return resourceGuard.guard(info.parentId, (id) -> {
            try {

                Path systemDir = getSystemDir(info.parentId,info.entityType);
                Path systemFile = getSystemFile(info.parentId, systemDir);

                if (Files.exists(systemFile))
                    fileUtil.backup(systemFile,systemDir);

                infoWriter.writeValue(systemFile.toFile(), info);
                return info;
            } catch (IOException e) {
                throw new ServerSideException("Cannot access system info: "+e.getMessage(),e);
            }
       });
    }
    
    
    protected Optional<SystemInfo> findSysInfo(long expId,Path systemDir)  {
        
       return resourceGuard.guard(expId, (id) -> {
            try {
        
            Path systemFile = getSystemFile(expId, systemDir);
            
            if (!Files.isRegularFile(systemFile))
                return Optional.<SystemInfo>empty();
            
            return Optional.<SystemInfo>of(infoReader.readValue(systemFile.toFile()));
            
            
            } catch (IOException e) {
                throw new ServerSideException("Cannot access system info: "+e.getMessage(),e);
            }
       });
    }    
    
     
    
    
    protected Path getSystemDir(long expId,EntityType type)  {
        
        Path p = null;
        switch (type) {
            case EXP_ASSAY :  {
                p = expStorage.getExperimentDir(expId).resolve(SYSTEM_DIR);
                break;
            }
            default: throw new IllegalArgumentException("Unsuported enitity type: "+type);
        }
        
        try {
            if (!Files.isDirectory(p))
                Files.createDirectories(p);
            return p;
        } catch (IOException  e) {
            throw new ServerSideException("Cannot get sytem dir: "+e.getMessage(),e);
        }
    } 
    
    protected Path getSystemFile(long expId,Path systemDir) throws IOException {
        String name = expId+SYSTEM_SUFFIX;
        return systemDir.resolve(name);
    }



    
}
