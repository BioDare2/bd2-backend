/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.backend.features.search.ExperimentVisibility;
import ed.biodare2.backend.features.search.SortOption;
import static ed.biodare2.backend.features.search.lucene.Fields.*;
import static ed.biodare2.backend.features.search.lucene.LuceneWriter.configStorage;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class LuceneExperimentsSearcherTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    Path indexDir;  
    LuceneWriter writer;
    List<Document> docs;
    LuceneSearcher searcher;
    LuceneExperimentsSearcher instance;
    
    public LuceneExperimentsSearcherTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        indexDir = testFolder.newFolder().toPath();
        
        writer = new LuceneWriter(indexDir);
        
        docs = SearchTestUtil.testDocuments();
        
        writer.indexWriter.addDocuments(docs);
        writer.indexWriter.commit();
        makeInstance();
    }
    
    @After
    public void close() throws Exception {
        writer.close();
        if (searcher != null) {
            searcher.close();
        }
    }
    
    void initSearcher() throws IOException {
        searcher = new LuceneSearcher(DirectoryReader.open(writer.indexWriter));
    }
    
    LuceneExperimentsSearcher makeInstance() throws IOException {
        initSearcher();
        instance = new LuceneExperimentsSearcher(searcher);
        return instance;
    }

    @Test
    public void setUpWorks() throws Exception {
        assertEquals(5, docs.size());
        
        // initSearcher();
        
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            TopDocs results = index.search(query, 10);
            assertEquals(docs.size(), results.totalHits.value);
        } finally {
          searcher.searcherManager.release(index);
        }
        
    }
    
    @Test
    public void visibilityFilterGivesNoneOnEmpty() throws IOException {
        

        ExperimentVisibility visiblity = new ExperimentVisibility();
        visiblity.showPublic = false;
        
        Query query = instance.visibilityFilter(visiblity);
        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            TopDocs hits = index.search(query, 10);
            assertEquals(0, hits.totalHits.value);
            assertEquals(0, hits.scoreDocs.length);
        } finally {
          searcher.searcherManager.release(index);
        }    
    }
    
    @Test
    public void visibilityFilterGivesOwned() throws IOException {
        

        ExperimentVisibility visibility = new ExperimentVisibility();
        visibility.showPublic = false;
        visibility.user = Optional.of("demo1");
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = instance.visibilityFilter(visibility);            
            TopDocs hits = index.search(query, 10);
            assertEquals(3, hits.totalHits.value);
            assertEquals(3, hits.scoreDocs.length);
            
            visibility.showPublic = true;
            query = instance.visibilityFilter(visibility);            
            hits = index.search(query, 10);
            assertEquals(4, hits.totalHits.value);
            assertEquals(4, hits.scoreDocs.length);            
        } finally {
          searcher.searcherManager.release(index);
        }    
    }  
    
    @Test
    public void sortByRankGivesEmptySort() throws IOException {
        
        SortOption option = SortOption.RANK;
        boolean asc = true;
        
        Optional<Sort> sort = instance.sortCriteria(option, asc);
        assertTrue(sort.isEmpty());
        
    }      
    
    @Test
    public void sortByIdSortsByNumericalIds() throws IOException {
        

        SortOption option = SortOption.ID;
        boolean asc = true;
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = new MatchAllDocsQuery();
            
            Sort sort = instance.sortCriteria(option, asc).get();            
            TopDocs hits = index.search(query, 10, sort);
            assertEquals(5, hits.totalHits.value);
            assertEquals(5, hits.scoreDocs.length);
            
            List<Long> ids = extractdIds(hits, index);
            List<Long> exp = List.of(1L, 2L, 13L, 14L, 25L);
            
            assertEquals(exp, ids);
            
            sort = instance.sortCriteria(option, !asc).get();            
            hits = index.search(query, 10, sort);
            ids = extractdIds(hits, index);
            
            exp = new ArrayList<>(exp);
            Collections.reverse(exp);
            assertEquals(exp, ids);
           
        } finally {
          searcher.searcherManager.release(index);
        }    
    }  
    
    @Test
    public void sortByNameSortsAlfabetically() throws IOException {
        

        SortOption option = SortOption.NAME;
        boolean asc = true;
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = new MatchAllDocsQuery();
            
            Sort sort = instance.sortCriteria(option, asc).get();            
            TopDocs hits = index.search(query, 10, sort);
            assertEquals(5, hits.totalHits.value);
            assertEquals(5, hits.scoreDocs.length);
            
            List<Long> ids = extractdIds(hits, index);
            List<Long> exp = List.of(2L, 13L, 25L, 14L, 1L);
            
            assertEquals(exp, ids);
            
            sort = instance.sortCriteria(option, !asc).get();            
            hits = index.search(query, 10, sort);
            ids = extractdIds(hits, index);
            
            exp = new ArrayList<>(exp);
            Collections.reverse(exp);
            assertEquals(exp, ids);
           
        } finally {
          searcher.searcherManager.release(index);
        }    
    }    
    
    @Test
    public void sortByFirstSortsAlfabetically() throws IOException {
        

        SortOption option = SortOption.FIRST_AUTHOR;
        boolean asc = true;
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = new MatchAllDocsQuery();
            
            Sort sort = instance.sortCriteria(option, asc).get();            
            TopDocs hits = index.search(query, 10, sort);
            assertEquals(5, hits.totalHits.value);
            assertEquals(5, hits.scoreDocs.length);
            
            List<Long> ids = extractdIds(hits, index);
            List<Long> exp = List.of(25L, 14L, 13L, 1L, 2L);
            
            assertEquals(exp, ids);
            
            sort = instance.sortCriteria(option, !asc).get();            
            hits = index.search(query, 10, sort);
            ids = extractdIds(hits, index);
            
            exp = new ArrayList<>(exp);
            Collections.reverse(exp);
            assertEquals(exp, ids);
           
        } finally {
          searcher.searcherManager.release(index);
        }    
    }     
    
    @Test
    public void sortByUploadedSortsByDate() throws IOException {
        

        SortOption option = SortOption.UPLOAD_DATE;
        boolean asc = true;
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = new MatchAllDocsQuery();
            
            Sort sort = instance.sortCriteria(option, asc).get();            
            TopDocs hits = index.search(query, 10, sort);
            
            List<Long> ids = extractdIds(hits, index);
            List<Long> exp = List.of(1L, 25L, 14L, 13L, 2L);
            
            assertEquals(exp, ids);
            
            sort = instance.sortCriteria(option, !asc).get();            
            hits = index.search(query, 10, sort);
            ids = extractdIds(hits, index);
            
            exp = new ArrayList<>(exp);
            Collections.reverse(exp);
            assertEquals(exp, ids);
           
        } finally {
          searcher.searcherManager.release(index);
        }    
    }     
    
    @Test
    public void sortByModifiedSortsByDate() throws IOException {
        

        SortOption option = SortOption.MODIFICATION_DATE;
        boolean asc = true;
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = new MatchAllDocsQuery();
            
            Sort sort = instance.sortCriteria(option, asc).get();            
            TopDocs hits = index.search(query, 10, sort);
            
            List<Long> ids = extractdIds(hits, index);
            List<Long> exp = List.of(1L, 14L, 25L, 13L, 2L);
            
            assertEquals(exp, ids);
            

           
        } finally {
          searcher.searcherManager.release(index);
        }    
    }     
    
    @Test
    public void sortByExecutedSortsByDate() throws IOException {
        

        SortOption option = SortOption.EXECUTION_DATE;
        boolean asc = true;
        

        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            
            Query query = new MatchAllDocsQuery();
            
            Sort sort = instance.sortCriteria(option, asc).get();            
            TopDocs hits = index.search(query, 10, sort);
            
            List<Long> ids = extractdIds(hits, index);
            List<Long> exp = List.of(1L, 25L, 2L, 13L, 14L);
            
            assertEquals(exp, ids);
            

           
        } finally {
          searcher.searcherManager.release(index);
        }    
    } 
    
    @Test
    public void findAllGivesAllVisible() throws IOException {
        

        ExperimentVisibility visibility = new ExperimentVisibility();
        visibility.showPublic = false;
        visibility.user = Optional.of("demo1");
        
        SortOption sorting = SortOption.RANK;
        boolean asc = true;        
        
        int pageIndex = 0;
        int pageSize = 10;
        
        ListWrapper<Long> ids = instance.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
        // List<Long> exp = List.of(1L, 2L, 13L, 25L, 14L);
        List<Long> exp = List.of(1L, 2L, 13L);
        
        assertEquals(exp, ids.data);
        
        visibility.showPublic = true;
        visibility.user = Optional.of("demo2");
        exp = List.of(2L, 25L, 14L);
        ids = instance.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
        assertEquals(exp, ids.data);
        
    }    

    protected List<Long> extractdIds(TopDocs hits, IndexSearcher index) throws IOException {
     
        List<Long> ids = new ArrayList<>(hits.scoreDocs.length);
        for (ScoreDoc hit: hits.scoreDocs) {
            Document doc = index.doc(hit.doc);
            ids.add(doc.getField(ID).numericValue().longValue());
        }
        
        return ids;
        
    }    
}
