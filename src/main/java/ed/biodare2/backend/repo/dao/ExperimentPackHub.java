/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.dao.AssayPackAssembler.AssayPackImpl;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentPackHub {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final AssayPackAssembler assembler;
    final SystemCopier copier;
    
    @Autowired
    ExperimentPackHub(AssayPackAssembler assembler,SystemCopier copier) {
        this.assembler = assembler;
        this.copier = copier;
    }
    
    public Optional<AssayPack> findOneForWriting(long expId) {
        
        return findOne(expId).map( this::enableWriting);
        
    }
    
    
    public AssayPack enableWriting(AssayPack pack) {
        //AssayPackImpl boundle = (AssayPackImpl)pack;
        AssayPackImpl boundle = new AssayPackImpl(pack.getId(),copier.copy(pack.getAssay()),copier.copy(pack.getSystemInfo()),copier.copy(pack.getDbSystemInfo())); 
        boundle.readOnly = false;
        return boundle;
    }
    
    
    public Optional<AssayPack> findOne(long expId) {
        
        return assembler.findOne(expId);
    }
    
    public Stream<AssayPack> findByIds(List<Long> expIds) {
        return findByIds(expIds.stream().mapToLong(l -> l));
    }
    
    /*public Stream<AssayPack> findByIds(Stream<Long> expIds) {
        
        return foldOptionals(expIds.map( this::findOne));
    }*/
    
    
    public Stream<AssayPack> findByIds(LongStream expIds) {
        return expIds
            .mapToObj(this::findOne)
            .filter(Optional::isPresent)
            .map(Optional::get);
    }    
    
    @Transactional
    public AssayPack save(AssayPack pack) {
        
        return assembler.save(pack);
        
    }
    
    public AssayPack newPack(ExperimentalAssay experiment,SystemInfo sysInfo,EntityACL acl) {
        Objects.requireNonNull(experiment);
        Objects.requireNonNull(sysInfo);
        Objects.requireNonNull(acl);
        
        sysInfo.parentId = experiment.getId();
        
        DBSystemInfo dbSystemInfo = new DBSystemInfo();
        dbSystemInfo.setEntityType(EntityType.EXP_ASSAY);
        dbSystemInfo.setParentId(experiment.getId());
        dbSystemInfo.setAcl(acl);
        
        AssayPackImpl pack = new AssayPackImpl(experiment.getId(),experiment,sysInfo,dbSystemInfo);
        pack.readOnly = false;
        return pack;
    }
    

    
  
}
