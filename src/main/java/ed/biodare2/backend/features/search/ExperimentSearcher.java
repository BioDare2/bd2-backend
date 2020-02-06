/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.features.search.lucene.LuceneExperimentsSearcher;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    final DBSystemInfoRep dbSystemInfos;  
    final LuceneExperimentsSearcher luceneExperimentsSearcher;
    
    @Autowired
    public ExperimentSearcher(DBSystemInfoRep dbSystemInfos, LuceneExperimentsSearcher luceneExperimentsSearcher) {
        this.dbSystemInfos = dbSystemInfos;
        this.luceneExperimentsSearcher = luceneExperimentsSearcher;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public ListWrapper<Long> findAllVisible(BioDare2User user, boolean showPublic, 
            int pageIndex, int pageSize) {
        
        return findAllVisible(user, showPublic, 
                SortOption.MODIFICATION_DATE, false, 
                pageIndex, pageSize);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public ListWrapper<Long> findAllVisible(BioDare2User user, boolean showPublic,
            SortOption sorting, boolean asc, 
            int pageIndex, int pageSize) {
        
        log.info("\nSEARCHING {} {}", user.getLogin(), showPublic);
        ExperimentVisibility visibility = new ExperimentVisibility();
        if (!user.isAnonymous() && (user.getId() != null)) {
            visibility.user = Optional.of(user.getLogin());
        }
        visibility.showPublic = showPublic;

        log.info("\nSEARCHING visibility {}", visibility);
        
        return luceneExperimentsSearcher.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
        
    }
    
    
    /*@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public LongStream findByOwner(BioDare2User owner) {
        
        //anonymous user
        if (owner.getId() == null) return LongStream.empty();
        
        return dbSystemInfos.findByOwnerIdAndEntityType(owner.getId(),EntityType.EXP_ASSAY)                
                .mapToLong(db -> db.getParentId());
    }  
    
    public LongStream findPublic() {
        
        return dbSystemInfos.findByOpenAndEntityType(EntityType.EXP_ASSAY)
                .mapToLong( db -> db.getParentId());
    }*/
}
