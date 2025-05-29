/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.features.search.lucene.LuceneExperimentsSearcher;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.util.Optional;
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
    final DBExperimentsSearcher dbExperimentsSearcher;  
    final LuceneExperimentsSearcher luceneExperimentsSearcher;
    
    @Autowired
    public ExperimentSearcher(DBExperimentsSearcher dbExperimentsSearcher, LuceneExperimentsSearcher luceneExperimentsSearcher) {
        this.dbExperimentsSearcher = dbExperimentsSearcher;
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
        
        //log.info("\nSEARCHING IN DB {} {}", user.getLogin(), showPublic);
        
        Optional<Long> userId = Optional.empty();
        if (!user.isAnonymous() && (user.getId() != null)) {
            userId = Optional.of(user.getId());
        }

        return dbExperimentsSearcher.findAllVisible(userId, showPublic, sorting, asc, pageIndex, pageSize);
        
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public ListWrapper<Long> findVisible(String query,
            String speciesName, String author, String fromCreationDate, String toCreationDate, String dataCategory,
            BioDare2User user, boolean showPublic,
            SortOption sorting, boolean asc, 
            int pageIndex, int pageSize) {
        
        //log.info("\nSEARCHING {} {} q:{}", user.getLogin(), showPublic, query);
        ExperimentVisibility visibility = new ExperimentVisibility();
        if (!user.isAnonymous() && (user.getId() != null)) {
            visibility.user = Optional.of(user.getLogin());
        }
        visibility.showPublic = showPublic;

        //log.info("\nSEARCHING visibility {}", visibility);
        
        return luceneExperimentsSearcher.findVisible(query, speciesName, author, fromCreationDate, toCreationDate, dataCategory, visibility, sorting, asc, pageIndex, pageSize);
        
    }    
    

}
