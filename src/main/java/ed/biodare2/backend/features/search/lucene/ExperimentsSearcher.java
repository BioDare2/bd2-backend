/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.backend.features.search.ExperimentVisibility;
import ed.biodare2.backend.features.search.SortOption;
import static ed.biodare2.backend.features.search.lucene.Fields.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PreDestroy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author tzielins
 */
public class ExperimentsSearcher implements AutoCloseable {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    final LuceneSearcher searcher;
    
    final static int MAX_HITS = 10_000;
    
    @Autowired
    public ExperimentsSearcher(LuceneSearcher searcher) {
        this.searcher = searcher;
    }
    
    @Override
    @PreDestroy
    public void close() throws Exception {
        searcher.close();
        log.info("Searcher closed");        
    }
    
    public List<Long> findAll(ExperimentVisibility visibility, SortOption sort) {
        return List.of();
    }
    
    
    protected Query visibilityFilter(ExperimentVisibility visibility) {
        
        Query personal;
        
        if (visibility.user.isPresent()) {
            Term ownerTerm = new Term(OWNER, visibility.user.get());
            personal = new TermQuery(ownerTerm);
        } else {
            personal = new MatchNoDocsQuery(); 
        }
        
        Query isPublic;
        if (visibility.showPublic) {
            isPublic = new TermQuery(new Term(IS_PUBLIC, ""+true));
        } else {
            isPublic = new MatchNoDocsQuery();
        }
        
        return new BooleanQuery.Builder()
                .add(personal, BooleanClause.Occur.SHOULD )
                .add(isPublic, BooleanClause.Occur.SHOULD )
                .build();
    }
    
    protected Optional<Sort> sortCriteria(SortOption options, boolean asc) {
     
        switch(options) {
            case RANK: return Optional.empty();
            case ID: return Optional.of(new Sort(new SortField(EXP_ID_S, SortField.Type.LONG, !asc)));
            case NAME: return Optional.of(new Sort(new SortField(NAME_S, SortField.Type.STRING, !asc)));
            case FIRST_AUTHOR: return Optional.of(new Sort(new SortField(FIRST_AUTHOR_S, SortField.Type.STRING, !asc)));
            case UPLOAD_DATE: return Optional.of(new Sort(new SortField(UPLOADED_S, SortField.Type.LONG, !asc)));
            case MODIFICATION_DATE: return Optional.of(new Sort(new SortField(MODIFIED_S, SortField.Type.LONG, !asc)));
            case EXECUTION_DATE: return Optional.of(new Sort(new SortField(EXECUTED_S, SortField.Type.LONG, !asc)));
            default: throw new IllegalArgumentException("Unsuported sorting option "+options);
        }
    }
    

}
