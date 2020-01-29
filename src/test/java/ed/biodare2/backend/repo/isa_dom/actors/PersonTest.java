/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.actors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class PersonTest {
    
    public PersonTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        Person org = new Person();
        org.login = "test";
        org.id = 12L;
        org.ORCID = "123-124-123-123";
        org.firstName ="Tomasz";
        org.lastName ="Zieli";
        //org.externalPath = "biodare/account/test";
        //org.externalService = "biodare2";
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        Person cpy = mapper.readValue(json, Person.class); 
        assertEquals(org.id ,cpy.id);
        assertEquals(org.login ,cpy.login);
        assertEquals(org.ORCID ,cpy.ORCID);
        assertEquals(org.firstName ,cpy.firstName);
        assertEquals(org.lastName ,cpy.lastName);
        assertEquals(org,cpy);
        
    }    
    
    @Test
    public void joinsNames() {
        List<Person> people = List.of( DomRepoTestBuilder.makePerson("T"),DomRepoTestBuilder.makePerson("S"));
        
        String res = Person.joinNames(people);
        assertEquals("FirstT LastT, FirstS LastS", res);
    }    
}
