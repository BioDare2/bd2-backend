/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.actors;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class PersonTest {
    
    public PersonTest() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

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
