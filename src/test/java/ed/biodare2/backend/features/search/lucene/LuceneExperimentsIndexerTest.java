/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeContributionDesc;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeExperimentalAssay;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makePerson;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import static ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder.makeSystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.time.LocalDate;
import java.util.List;
import org.apache.lucene.document.Document;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class LuceneExperimentsIndexerTest {
    
    public LuceneExperimentsIndexerTest() {
    }
    
    LuceneWriter writer;
    LuceneExperimentsIndexer instance;
    LuceneSearcher searcher;
    
    @Before
    public void setUp() {
        
        writer = mock(LuceneWriter.class);
        searcher = mock(LuceneSearcher.class);
        instance = new LuceneExperimentsIndexer(writer, searcher);
    }

    @Test
    public void authorsJoinNames() {
        
        ContributionDesc desc = makeContributionDesc();
        desc.authors.add(makePerson("Tomek"));
        
        String resp = instance.authors(desc);
        
        assertTrue(resp.contains("Firsttest"));
        assertTrue(resp.contains("LastTomek"));
        
    }
    
    @Test
    public void wholeContentContainsTheDetails() {
        
        ExperimentalAssay exp = makeExperimentalAssay();
        String resp = instance.wholeContent(exp);
        
        // System.out.println(resp);
        
        assertTrue(resp.startsWith(exp.getId()+" "));
        
        assertTrue(resp.contains("Test experiment"));
        assertTrue(resp.contains("To check code"));
        assertTrue(resp.contains("A commment"));
        assertTrue(resp.contains("A description"));

        assertTrue(resp.contains(LocalDate.now().toString()));
        
    }
    
    @Test
    public void createsDocumentBasedOnExp() throws Exception {
        
       ExperimentalAssay exp = makeExperimentalAssay();
       SystemInfo sys = makeSystemInfo();
       
       Document doc = instance.prepareDocument(exp, sys);
       assertNotNull(doc);
       
       List<String> allFields = Fields.allFields();
       
       allFields.forEach( f -> {
           assertNotNull(f, doc.getField(f));
       });
       
    }
}
