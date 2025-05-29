/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.backend.features.search.ExperimentVisibility;
import ed.biodare2.backend.features.search.SortOption;
import static ed.biodare2.backend.features.search.lucene.Fields.*;
import static ed.biodare2.backend.features.search.lucene.LuceneConfiguration.configAnalyser;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.biodare2.backend.web.rest.ListWrapper;

import java.io.IOException;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import jakarta.annotation.PreDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.document.LongPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
/**
 *
 * @author tzielins
 */
@Service
public class LuceneExperimentsSearcher implements AutoCloseable {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    final LuceneSearcher searcher;
    final Analyzer analyzer;
    
    final static int MAX_HITS = 10_000;
    
    @Autowired
    public LuceneExperimentsSearcher(LuceneSearcher searcher) {
        this.searcher = searcher;
        this.analyzer = configAnalyser();
    }
    
    @Override
    @PreDestroy
    public void close() throws Exception {
        searcher.close();
        log.info("Searcher closed");        
    }
    
    public ListWrapper<Long> findAllVisible(ExperimentVisibility visibility, 
            SortOption sorting, boolean asc, int pageIndex, int pageSize) {
        
        Query query = new MatchAllDocsQuery();
        String speciesName = "";
        String author = "";
        String fromCreationDate = "";
        String toCreationDate = "";
        String dataCategory = "";  
        return find(query, speciesName, author, fromCreationDate, toCreationDate, dataCategory, visibility, sorting, asc, pageIndex, pageSize);
    }
    
    public ListWrapper<Long> findVisible(String queryString,
            String speciesName, String author, String fromCreationDate, String toCreationDate, String dataCategory,
            ExperimentVisibility visibility, 
            SortOption sorting, boolean asc, int pageIndex, int pageSize) {
        
        Query query = parseQuery(queryString); 
        // log.info("\nWill searech for:\n{}\n\n",query.toString());
        return find(query, speciesName, author, fromCreationDate, toCreationDate, dataCategory, visibility, sorting, asc, pageIndex, pageSize);
    }    
    
    protected ListWrapper<Long> find(Query query,
            String speciesName, String author, String fromCreationDate, String toCreationDate, String dataCategory,
            ExperimentVisibility visibility, 
            SortOption sorting, boolean asc, int pageIndex, int pageSize) {

        query = addAdvancedFilters(query, speciesName, author, fromCreationDate, toCreationDate, dataCategory);
        query = addVisibilityFilter(query, visibility);        
        Optional<Sort> sort = sortCriteria(sorting, asc);
                
        return searcher.search(query, sort, pageIndex, pageSize);
    }

    protected Query advancedFilters(String speciesName, String author, String fromCreationDate, String toCreationDate, String dataCategory) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        if (!speciesName.isEmpty()) {
            Term speciesTerm = new Term(SPECIES, speciesName);
            builder.add(new TermQuery(speciesTerm), BooleanClause.Occur.MUST);
        }
    
        if (!author.isEmpty()) {
        try {
            QueryParser parser = new QueryParser(AUTHORS, analyzer);
            Query authorQuery = parser.parse(author);
            builder.add(authorQuery, BooleanClause.Occur.MUST);
        } catch (ParseException e) {
            log.error("Error parsing author query: {}", author, e);
        }
        }
    
        if (!fromCreationDate.isEmpty() || !toCreationDate.isEmpty()) {
            long fromEpoch = fromCreationDate.isEmpty() 
                ? Long.MIN_VALUE 
                : LocalDate.parse(fromCreationDate).toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
            long toEpoch = toCreationDate.isEmpty() 
                ? Long.MAX_VALUE 
                : LocalDate.parse(toCreationDate).toEpochSecond(LocalTime.MAX, ZoneOffset.UTC);

            builder.add(
                LongPoint.newRangeQuery(
                    EXECUTED,
                    fromEpoch,
                    toEpoch
                ),
                BooleanClause.Occur.MUST
            );
        }
    
        if (!dataCategory.isEmpty()) {
            try {
                // Use the analyzer to tokenize the input
                TokenStream tokenStream = analyzer.tokenStream(DATA_CATEGORY, dataCategory);
                tokenStream.reset();

                PhraseQuery.Builder phraseBuilder = new PhraseQuery.Builder();
                while (tokenStream.incrementToken()) {
                    String term = tokenStream.getAttribute(CharTermAttribute.class).toString();
                    phraseBuilder.add(new Term(DATA_CATEGORY, term));
                }
                tokenStream.end();
                tokenStream.close();

                builder.add(phraseBuilder.build(), BooleanClause.Occur.MUST);
            } catch (IOException e) {
                log.error("Error building PhraseQuery for Data Category: {}", dataCategory, e);
            }
        }

        if (builder.build().clauses().isEmpty()) {
            return new MatchAllDocsQuery();
        }
    
        return builder.build();
    }
    
    Query addAdvancedFilters(Query query, String speciesName, String author, String fromCreationDate, String toCreationDate, String dataCategory) {
        Query advancedFilter = advancedFilters(speciesName, author, fromCreationDate, toCreationDate, dataCategory);
        
        return new BooleanQuery.Builder()
                .add(query, BooleanClause.Occur.MUST)
                .add(advancedFilter, BooleanClause.Occur.FILTER)
                .build();
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
    
    Query addVisibilityFilter(Query query, ExperimentVisibility visibility) {
        
        Query visiblityFilter = visibilityFilter(visibility);
        
        return new BooleanQuery.Builder()
                .add(query, BooleanClause.Occur.MUST )
                .add(visiblityFilter, BooleanClause.Occur.FILTER )
                .build();
    }    
    
    protected Optional<Sort> sortCriteria(SortOption options, boolean asc) {
     
        switch(options) {
            case RANK: return Optional.empty();
            case ID: return Optional.of(new Sort(new SortField(ID_S, SortField.Type.LONG, !asc)));
            case NAME: return Optional.of(new Sort(new SortField(NAME_S, SortField.Type.STRING, !asc)));
            case FIRST_AUTHOR: return Optional.of(new Sort(new SortField(FIRST_AUTHOR_S, SortField.Type.STRING, !asc)));
            case UPLOAD_DATE: return Optional.of(new Sort(new SortField(UPLOADED_S, SortField.Type.LONG, !asc)));
            case MODIFICATION_DATE: return Optional.of(new Sort(new SortField(MODIFIED_S, SortField.Type.LONG, !asc)));
            case EXECUTION_DATE: return Optional.of(new Sort(new SortField(EXECUTED_S, SortField.Type.LONG, !asc)));
            default: throw new IllegalArgumentException("Unsuported sorting option "+options);
        }
    }

    Query parseQuery(String queryString) {
        
        String[] fields = {NAME, PURPOSE, AUTHORS, WHOLE_CONTENT};
        BooleanClause.Occur[] flags = new BooleanClause.Occur[fields.length];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = BooleanClause.Occur.SHOULD;
        }
        
        try {
            return MultiFieldQueryParser.parse(queryString, fields, flags, analyzer);
        } catch (ParseException e) {
            throw new HandlingException("Could not parse query: " + queryString + "; " + e.getMessage(), e);
        }
    }

}