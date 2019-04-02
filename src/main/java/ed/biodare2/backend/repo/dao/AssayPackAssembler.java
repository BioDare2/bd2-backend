/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
@CacheConfig(cacheNames = {"AssayPack"})
public class AssayPackAssembler {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final ExperimentalAssayRep experiments;
    final SystemInfoRep systemInfos;
    final DBSystemInfoRep dbSystemInfos;

    
    @Autowired
    public AssayPackAssembler(ExperimentalAssayRep experiments,SystemInfoRep systemInfos,DBSystemInfoRep dbSystemInfos) {
        this.experiments = experiments;
        this.systemInfos = systemInfos;
        this.dbSystemInfos = dbSystemInfos;
        
    }
    
    @Cacheable(unless="#result == null")
    @Transactional(propagation = Propagation.REQUIRES_NEW) //hopefully it will detach the entry at exit
    public Optional<AssayPack> findOne(long expId) {
        
        try {
            return Optional.of(assemble(expId));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
        
    }
    
    protected AssayPackImpl assemble(long expId) {
        Optional<DBSystemInfo> dbSystemInfo = dbSystemInfos.findByParentIdAndEntityType(expId, EntityType.EXP_ASSAY);
        
        Optional<SystemInfo> systemInfo = systemInfos.findByParent(expId, EntityType.EXP_ASSAY);
        
        Optional<ExperimentalAssay> assay = experiments.findOne(expId);
        
        if (dbSystemInfo.isPresent() && systemInfo.isPresent() && assay.isPresent()) {
            return new AssayPackImpl(expId, assay.get(), systemInfo.get(), dbSystemInfo.get());
        }
        
        boolean any = dbSystemInfo.isPresent() || systemInfo.isPresent() || assay.isPresent();
        if (any) {
            log.warn("Partial system data found for experiment {}: DB:{}, SYS:{} ASSAY:{}",expId,dbSystemInfo.isPresent(),systemInfo.isPresent(),assay.isPresent());
        }
        
        throw new NotFoundException("ExperimentFragments for "+expId+" not found; partial info: "+any);
        
    }
    
    
    @Transactional
    @CachePut(key="#pack.getId()")
    public AssayPack save(AssayPack pack) {
        
        AssayPackImpl boundle;
        if (!(pack instanceof AssayPackImpl)) {
            boundle = new AssayPackImpl(pack);
        } else {
            boundle = (AssayPackImpl)pack;
        }
        
        if (boundle.readOnly)
            throw new IllegalArgumentException("Trying to save readonly pack, remember to retrieve it in 'write mode' for modifications");
        
        if (boundle.assay.getId() != boundle.expId)
            throw new IllegalArgumentException("Mismatch between expIds inside boundle");
        if (boundle.systemInfo.parentId != boundle.expId)
            throw new IllegalArgumentException("Mismatch between expIds inside boundle");
        if (boundle.dbSystemInfo.getParentId() != boundle.expId)
            throw new IllegalArgumentException("Mismatch between expIds inside boundle");
        
        boundle.dbSystemInfo = dbSystemInfos.save(boundle.dbSystemInfo);
        boundle.assay = experiments.save(boundle.assay);
        boundle.systemInfo = systemInfos.save(boundle.systemInfo);
        boundle.readOnly = true;
        return boundle;
    }
    
    static class AssayPackImpl implements AssayPack {

        long expId;
        boolean readOnly;
        DBSystemInfo dbSystemInfo;
        SystemInfo systemInfo;
        ExperimentalAssay assay;
        
        AssayPackImpl(long expId,ExperimentalAssay experiment,SystemInfo systemInfo,DBSystemInfo dbSystemInfo) {
            this.readOnly = true;
            this.expId = expId;
            this.dbSystemInfo = Objects.requireNonNull(dbSystemInfo);
            this.systemInfo = Objects.requireNonNull(systemInfo);
            this.assay = Objects.requireNonNull(experiment);        
        }        
        
        AssayPackImpl(AssayPack pack) {
            this(pack.getId(),pack.getAssay(),pack.getSystemInfo(),pack.getDbSystemInfo());
        }
        
        @Override
        public SystemInfo getSystemInfo() {
            return systemInfo;
        }

        @Override
        public DBSystemInfo getDbSystemInfo() {
            return dbSystemInfo;
        }

        @Override
        public ExperimentalAssay getAssay() {
            return assay;
        }

        @Override
        public long getId() {
            return expId;
        }
        
        void setId(long expId) {
            this.expId = expId;
            this.systemInfo.parentId = expId;
            this.dbSystemInfo.setParentId(expId);
            this.assay.setId(expId);
        }        

        @Override
        public void setAssay(ExperimentalAssay exp) {
            this.assay = exp;
        }
        
                
    }    
}
