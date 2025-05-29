/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.backend.web.rest.ListWrapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class LuceneSearcherTest {
   
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    Path indexDir;  
    LuceneWriter writer;
    List<Document> docs;
    LuceneSearcher searcher;

    
    public LuceneSearcherTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        indexDir = testFolder.newFolder().toPath();
        
        writer = new LuceneWriter(indexDir);
        
        docs = SearchTestUtil.testDocuments();
        
        writer.indexWriter.addDocuments(docs);
        writer.indexWriter.commit();
        
        searcher = new LuceneSearcher(writer);
    }
    
    @After
    public void close() throws Exception {
        writer.close();
        searcher.close();
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
    public void extractIndexAppliesPagination() throws Exception {
        
        IndexSearcher index  = searcher.searcherManager.acquire();
        try {
            MatchAllDocsQuery query = new MatchAllDocsQuery();            
            TopDocs hits = index.search(query, 10);
            
            int pageIndex = 2;
            int pageSize = 10;
            List<Long> ids = searcher.extractIds(hits, pageIndex, pageSize, index);
            assertTrue(ids.isEmpty());
            
            pageIndex = 0;
            ids = searcher.extractIds(hits, pageIndex, pageSize, index);
            List<Long> exp = List.of(1L, 2L, 13L, 25L, 14L);
            assertEquals(exp, ids);
            
            pageIndex = 1;
            pageSize = 2;
            ids = searcher.extractIds(hits, pageIndex, pageSize, index);
            exp = List.of(13L, 25L);
            assertEquals(exp, ids);
            
        } finally {
          searcher.searcherManager.release(index);
        }
        
    }

    @Test    
    public void searchNoSortCallsSearch() throws IOException {
        IndexSearcher index = mock(IndexSearcher.class);
        
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        int maxHits = 10;
        
        TopDocs allDocs = new TopDocs(null, new ScoreDoc[0]);
        when(index.search(query, maxHits)).thenReturn(allDocs);
        
        TopDocs hits = searcher.search(index, query, maxHits);
        assertSame(allDocs, hits);
        
        verify(index).search(query, maxHits);
    }
    
    @Test    
    public void searchWithSortCallsSearch() throws IOException {
        IndexSearcher index = mock(IndexSearcher.class);
        
        MatchAllDocsQuery query = new MatchAllDocsQuery();
        int maxHits = 10;
        Sort sort = new Sort();
        
        TopFieldDocs allDocs = new TopFieldDocs(null, new ScoreDoc[0], new SortField[0]);
        when(index.search(eq(query), anyInt(), eq(sort))).thenReturn(allDocs);
        
        TopDocs hits = searcher.search(index, query, sort, maxHits);
        assertSame(allDocs, hits);
        
        verify(index).search(any(), anyInt(), any());
    }    
    
    // @Test    
    // public void searchWithSortAsksForMoreHitsAndThrowsExceptionIfMoreHitsAvailable() throws IOException {
    //     IndexSearcher index = mock(IndexSearcher.class);
        
    //     MatchAllDocsQuery query = new MatchAllDocsQuery();
    //     int maxHits = 10;
    //     Sort sort = new Sort();
        
    //     TopFieldDocs allDocs = new TopFieldDocs(null, new ScoreDoc[maxHits+1], new SortField[0]);
    //     when(index.search(eq(query), eq(maxHits+1), eq(sort))).thenReturn(allDocs);
        
    //     try {
    //         TopDocs hits = searcher.search(index, query, sort, maxHits);
    //         assertSame(allDocs, hits);
    //         fail("Exceptin expected");
    //     } catch (IllegalStateException e) {};
        
    //     verify(index).search(any(), anyInt(), any());
    // }    
    
    @Test
    public void searchReturnsPagedIds() throws Exception {
        
        MatchAllDocsQuery query = new MatchAllDocsQuery();               
        Optional<Sort> sort = Optional.empty();
        int maxHits = 10;

        int pageIndex = 1;
        int pageSize = 2;

        ListWrapper<Long> ids = searcher.search(query, sort, pageIndex, pageSize, maxHits);
        //List<Long> exp = List.of(1L, 2L, 13L, 25L, 14L);
        List<Long> exp = List.of(13L, 25L);

        assertEquals(exp, ids.data);
        assertEquals(1, ids.currentPage.pageIndex);
        assertEquals(2, ids.currentPage.pageSize);
        assertEquals(5, ids.currentPage.length);
        
        pageIndex = 0;
        pageSize = 10;
        ids = searcher.search(query, sort, pageIndex, pageSize, maxHits);
        exp = List.of(1L, 2L, 13L, 25L, 14L);
        assertEquals(exp, ids.data);
        assertEquals(0, ids.currentPage.pageIndex);
        assertEquals(10, ids.currentPage.pageSize);
        assertEquals(5, ids.currentPage.length);
    } 
    
    @Test
    public void searchMovesPageToZeroIfOutsideBounds() throws Exception {
        
        MatchAllDocsQuery query = new MatchAllDocsQuery();               
        Optional<Sort> sort = Optional.empty();
        int maxHits = 10;

        int pageIndex = 10;
        int pageSize = 2;

        ListWrapper<Long> ids = searcher.search(query, sort, pageIndex, pageSize, maxHits);
        //List<Long> exp = List.of(1L, 2L, 13L, 25L, 14L);
        List<Long> exp = List.of(1L, 2L);

        assertEquals(exp, ids.data);
        assertEquals(0, ids.currentPage.pageIndex);
        assertEquals(2, ids.currentPage.pageSize);
        assertEquals(5, ids.currentPage.length);
            
    } 

   
    
}
