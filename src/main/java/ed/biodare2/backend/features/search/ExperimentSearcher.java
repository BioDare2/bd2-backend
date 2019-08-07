/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentSearcher {
    
    final DBSystemInfoRep dbSystemInfos;    
    
    @Autowired
    public ExperimentSearcher(DBSystemInfoRep dbSystemInfos) {
        this.dbSystemInfos = dbSystemInfos;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public LongStream findByOwner(BioDare2User owner) {
        
        //anonymous user
        if (owner.getId() == null) return LongStream.empty();
        
        return dbSystemInfos.findByOwnerIdAndEntityType(owner.getId(),EntityType.EXP_ASSAY)                
                .mapToLong(db -> db.getParentId());
    }  
    
    public LongStream findPublic() {
        
        return dbSystemInfos.findByOpenAndEntityType(EntityType.EXP_ASSAY)
                .mapToLong( db -> db.getParentId());
    }
}
