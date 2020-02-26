/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.util.Optional;
import java.util.stream.LongStream;
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
        
        Page<Long> pagedIds = dbSystemInfos.findParentIdsByOpenOrOwnerIdAndEntityTypeWithPagination(ownerId, EntityType.EXP_ASSAY, showPublic, page);
        return new ListWrapper<>(pagedIds.getContent(), 
                pagedIds.getPageable().getPageNumber(), pagedIds.getPageable().getPageSize(), 
                pagedIds.getTotalElements());
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

    Sort sortCriteria(SortOption sorting, boolean asc) {
        
        Sort sort = sortOption2Sort(sorting);
        if (!asc) sort = sort.descending();
        return sort;
    }

    Sort sortOption2Sort(SortOption sorting) {
        
        switch(sorting) {
            case ID: return Sort.by("parentId");
            case RANK: return Sort.by("creationDate").descending();
            case NAME: return Sort.by("searchInfo.name");
            case FIRST_AUTHOR: return Sort.by("searchInfo.firstAuthor");
            case EXECUTION_DATE: return Sort.by("searchInfo.executionDate");
            case MODIFICATION_DATE: return Sort.by("searchInfo.modificationDate");
            case UPLOAD_DATE: return Sort.by("creationDate");
            default: throw new IllegalArgumentException("Unsuported sorting by: "+sorting);
        }
    }
}
