/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import static ed.biodare2.backend.features.search.lucene.Fields.*;
import static ed.biodare2.backend.features.search.lucene.SearchTestUtil.testDocuments;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import static ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder.makeSystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class LuceneExperimentsIndexerTest {

    @TempDir
    Path testFolder;
    
    public LuceneExperimentsIndexerTest() {
    }
    
    LuceneWriter writer;
    LuceneExperimentsIndexer instance;
    LuceneSearcher searcher;
    Path indexDir;
    
    @BeforeEach
    public void setUp() {
        writer = mock(LuceneWriter.class);
        searcher = mock(LuceneSearcher.class);
        instance = new LuceneExperimentsIndexer(writer, searcher);
    }
    
    void setUpReal() throws Exception {
        indexDir = testFolder.resolve("test");
        
        writer = new LuceneWriter(indexDir);   
        searcher = new LuceneSearcher(writer);
    }
    
    @AfterEach
    public void close() throws IOException {
        writer.close();
        searcher.close();
    }

    
    @Test
    public void createsDocumentBasedOnExp() throws Exception {
        
       ExperimentalAssay exp = makeExperimentalAssay();
       SystemInfo sys = makeSystemInfo();
       
       Document doc = instance.prepareDocument(exp, sys);
       assertNotNull(doc);
       
       List<String> allFields = Fields.allFields();
       
       allFields.forEach( f -> {
	       assertNotNull(doc.getField(f), f);
       });
       
    }
    
    @Test
    public void termFromStringSameAsLongId() throws Exception {
        
        setUpReal();
        
        Document doc = testDocuments().get(0);
        
        long uploadedO = doc.getField(UPLOADED).numericValue().longValue();
        
        LocalDateTime nowD = LocalDateTime.now();
        long uploadedN = nowD.toEpochSecond(ZoneOffset.UTC);
        
        assertNotEquals(uploadedN, uploadedO);
        
        // writer.indexWriter.addDocument(doc);
        writer.indexWriter.updateDocument(new Term(ID, ""+1L), doc);
        writer.indexWriter.commit();
        
        MatchAllDocsQuery all = new MatchAllDocsQuery();
        Query byId = new TermQuery(new Term(ID, ""+1L));
        Query byOrgUp = LongPoint.newExactQuery(UPLOADED, uploadedO);
        Query byNewUp = LongPoint.newExactQuery(UPLOADED, uploadedN);
        
        searcher.searcherManager.maybeRefreshBlocking();
        IndexSearcher indexSearcher = searcher.searcherManager.acquire();
        try {
            
            TopDocs hits = indexSearcher.search(all, 10);
            assertEquals(1, hits.scoreDocs.length);
            
            hits = indexSearcher.search(byId, 10);
            assertEquals(1, hits.scoreDocs.length);
            
            hits = indexSearcher.search(byOrgUp, 10);
            assertEquals(1, hits.scoreDocs.length);
            
            hits = indexSearcher.search(byNewUp, 10);
            assertEquals(0, hits.scoreDocs.length);
            
        } finally {
            searcher.searcherManager.release(indexSearcher);
        }
        
        doc.removeField(UPLOADED);
        doc.add(new LongPoint(UPLOADED, nowD.toEpochSecond(ZoneOffset.UTC)));
        
        writer.indexWriter.updateDocument(new Term(ID, ""+1L), doc);
        writer.indexWriter.commit();        
        
        searcher.searcherManager.maybeRefreshBlocking();
        indexSearcher = searcher.searcherManager.acquire();
        try {
            
            TopDocs hits = indexSearcher.search(all, 10);
            assertEquals(1, hits.scoreDocs.length);
            
            hits = indexSearcher.search(byId, 10);
            assertEquals(1, hits.scoreDocs.length);
            
            hits = indexSearcher.search(byOrgUp, 10);
            assertEquals(0, hits.scoreDocs.length);
            
            hits = indexSearcher.search(byNewUp, 10);
            assertEquals(1, hits.scoreDocs.length);
            
        } finally {
            searcher.searcherManager.release(indexSearcher);
        }
        
    }
}
