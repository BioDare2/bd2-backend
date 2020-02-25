/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class DBExperimentsSearcher {
    
    final DBSystemInfoRep dbSystemInfos;    
    
    @Autowired
    public DBExperimentsSearcher(DBSystemInfoRep dbSystemInfos) {
        this.dbSystemInfos = dbSystemInfos;
    }
    
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public ListWrapper<Long> findAllVisible(Optional<Long> user, boolean showPublic, 
            SortOption sorting, boolean asc, int pageIndex, int pageSize) {
        
        long ownerId = user.orElse(-1L);
        
        Sort sort = sortCriteria(sorting, asc);
        PageRequest page = PageRequest.of(pageIndex, pageSize, sort);
        
        Page<DBSystemInfo> paged = dbSystemInfos.findByOpenOrOwnerIdAndEntityTypeWithPagination(ownerId, EntityType.EXP_ASSAY, showPublic, page);
        List<Long> ids = paged.map(db -> db.getParentId()).toList();
        return new ListWrapper<>(ids, paged.getPageable().getPageNumber(), paged.getPageable().getPageSize(), paged.getTotalElements());
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

    private Sort sortCriteria(SortOption sorting, boolean asc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
