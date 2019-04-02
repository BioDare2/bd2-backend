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
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
class SystemCopier {
    
    final DBSystemInfoRep dbSystemInfos;
    
    final ObjectReader systemInfoReader;
    final ObjectWriter systemInfoWriter;   
    
    final ObjectReader experimentalAssayReader;
    final ObjectWriter experimentalAssayWriter;      
    
    public SystemCopier(DBSystemInfoRep dbSystemInfos,@Qualifier("PlainMapper") ObjectMapper mapper) {
        
        this.dbSystemInfos = dbSystemInfos;
        
        this.systemInfoReader = mapper.readerFor(SystemInfo.class);
        this.systemInfoWriter = mapper.writerFor(SystemInfo.class);
        
        this.experimentalAssayReader =  mapper.readerFor(ExperimentalAssay.class);
        this.experimentalAssayWriter = mapper.writerFor(ExperimentalAssay.class);
        
    }       
    
    public ExperimentalAssay copy(ExperimentalAssay org) {
      
        try {
            String json = experimentalAssayWriter.writeValueAsString(org);
            ExperimentalAssay copy = experimentalAssayReader.readValue(json);
            return copy;
        } catch (IOException e) {
            throw new ServerSideException("Cannot copy experimentalassay: "+e.getMessage(),e);
        }        
    }
    
    public SystemInfo copy(SystemInfo org) {
        
        try {
            String json = systemInfoWriter.writeValueAsString(org);
            SystemInfo copy = systemInfoReader.readValue(json);
            return copy;
        } catch (IOException e) {
            throw new ServerSideException("Cannot copy system info: "+e.getMessage(),e);
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW) //hopefully it will detach the entry
    public DBSystemInfo copy(DBSystemInfo org) {

        DBSystemInfo cpy = dbSystemInfos.findById(org.getInnerId())
                .orElseThrow(() -> new IllegalArgumentException("The db system info:"+org.getInnerId()+" for: "+org.getParentId()+" is not known in the system"));
        
        //fetching full acl is will be accessible outside transaction
        if (cpy.getAcl() != null) {
            cpy.getAcl().getAllowedToRead().size();
            cpy.getAcl().getAllowedToWrite().size();
        }
        return cpy;
    }
    
}
