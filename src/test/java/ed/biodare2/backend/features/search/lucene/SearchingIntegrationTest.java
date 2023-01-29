/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.search.ExperimentVisibility;
import ed.biodare2.backend.features.search.SortOption;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import static ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder.makeSystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.web.rest.ListWrapper;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
public class SearchingIntegrationTest {
    
    @Autowired
    LuceneExperimentsSearcher searcher;
    
    
    @Autowired
    LuceneExperimentsIndexer indexer;
    
    @Before
    public void setUp() throws Exception {
        
        indexer.writer.deleteAll();
        searcher.searcher.updateIndex();
    }
    
    @Test
    public void setUpWorks() throws Exception {
        
        IndexSearcher nat = new IndexSearcher(DirectoryReader.open(indexer.writer.indexWriter));
        try {
            Query query = new MatchAllDocsQuery();
            TopDocs hits = nat.search(query, 10);
            assertEquals(0, hits.scoreDocs.length);
        } finally {
            nat.getIndexReader().close();
        }
       
    }    
    
    @Test
    public void canFindWhatWasIndexed() {
        
       ExperimentalAssay exp = makeExperimentalAssay();
       SystemInfo sys = makeSystemInfo();
       sys.security.owner = "tomek";
       sys.security.isPublic = false;
       
       ExperimentVisibility visibility = new ExperimentVisibility("tomek");
       SortOption sorting = SortOption.RANK;
       boolean asc = true;
       int pageIndex = 0;
       int pageSize = 10;
       
       ListWrapper<Long> hits = searcher.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
       
       assertTrue(hits.data.isEmpty());
       
       indexer.indexExperiment(exp, sys);
       
       hits = searcher.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
       assertEquals(1, hits.data.size());
       
    }
    
    @Test
    public void canFindWhatWasUpdated() {
        
       ExperimentalAssay exp = makeExperimentalAssay();
       SystemInfo sys = makeSystemInfo();
       sys.security.owner = "tomek";
       sys.security.isPublic = false;
       
       ExperimentVisibility visibility = new ExperimentVisibility("tomek");
       SortOption sorting = SortOption.RANK;
       boolean asc = true;
       int pageIndex = 0;
       int pageSize = 10;
       
       ListWrapper<Long> hits = searcher.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
       
       assertTrue(hits.data.isEmpty());
       
       indexer.indexExperiment(exp, sys);
       
       exp.setId(exp.getId()+1);
       indexer.indexExperiment(exp, sys);
       
       
       hits = searcher.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
       assertEquals(2, hits.data.size());
       
       ExperimentVisibility visibility2 = new ExperimentVisibility("romek", true);
       hits = searcher.findAllVisible(visibility2, sorting, asc, pageIndex, pageSize);
       assertEquals(0, hits.data.size());
       
       sys.security.isPublic = true;       
       indexer.indexExperiment(exp, sys);
       hits = searcher.findAllVisible(visibility2, sorting, asc, pageIndex, pageSize);
       assertEquals(1, hits.data.size());
       
       hits = searcher.findAllVisible(visibility, sorting, asc, pageIndex, pageSize);
       assertEquals(2, hits.data.size());       
    }  
    
    @Test
    public void canSearchByQueryWhatWasIndexed() {
        
       ExperimentalAssay exp = makeExperimentalAssay();
       exp.generalDesc.name = "Testing searching for wanderland";
       SystemInfo sys = makeSystemInfo();
       sys.security.owner = "tomek";
       sys.security.isPublic = false;
       
       ExperimentVisibility visibility = new ExperimentVisibility("tomek");
       SortOption sorting = SortOption.RANK;
       boolean asc = true;
       int pageIndex = 0;
       int pageSize = 10;
       
       indexer.indexExperiment(exp, sys);
       
       ListWrapper<Long> hits = searcher.findVisible("missing", visibility, sorting, asc, pageIndex, pageSize);
       
       assertTrue(hits.data.isEmpty());
       
       
       hits = searcher.findVisible("wanderland", visibility, sorting, asc, pageIndex, pageSize);
       assertEquals(1, hits.data.size());
       
    }    
}
