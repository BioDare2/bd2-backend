/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeContributionDesc;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeExperimentalAssay;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makePerson;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class IndexingUtilTest {
    
    public IndexingUtilTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testSomeMethod() {
    }
    
    
    @Test
    public void authorsJoinNames() {
        
        ContributionDesc desc = makeContributionDesc();
        desc.authors.add(makePerson("Tomek"));
        
        String resp = IndexingUtil.authors(desc);
        
        assertTrue(resp.contains("Firsttest"));
        assertTrue(resp.contains("LastTomek"));
        
    }
    
    @Test
    public void wholeContentContainsTheDetails() {
        
        ExperimentalAssay exp = makeExperimentalAssay();
        String resp = IndexingUtil.wholeContent(exp);
        
        // System.out.println(resp);
        
        assertTrue(resp.startsWith(exp.getId()+" "));
        
        assertTrue(resp.contains("Test experiment"));
        assertTrue(resp.contains("To check code"));
        assertTrue(resp.contains("A commment"));
        assertTrue(resp.contains("A description"));

        assertTrue(resp.contains(LocalDate.now().toString()));
        
    }
    
    @Test
    public void trimTrims() {
        
        assertEquals("", IndexingUtil.trim(null, 2));
        assertEquals("al", IndexingUtil.trim("al", 3));
        assertEquals("ala", IndexingUtil.trim("alabc", 3));
    }
    
}
