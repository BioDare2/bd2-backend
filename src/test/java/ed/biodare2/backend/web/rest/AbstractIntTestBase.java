/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.security.BioDare2User;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Resource;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

/**
 *
 * @author tzielins
 */
public class AbstractIntTestBase {
     
    
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                        MediaType.APPLICATION_JSON.getSubtype(),                        
                                                                        Charset.forName("utf8")                     
                                                                        );    
    
    public static final TypeReference<Map<String,String>> MAP_TYPE = new TypeReference<Map<String,String>>() { };
    public static final List<GrantedAuthority> USER_ROLES = Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_READER","ROLE_USER","ROLE_WRITER"));    
    
    @Autowired 
    WebApplicationContext wac; 
    
    @Autowired 
    Fixtures fixtures;
    
    //@Autowired
    //@Qualifier("DomMapper") 
    @Resource(name = "DomMapper" )        
    ObjectMapper mapper;
    
    
    MockMvc mockMvc;
    
    BioDare2User currentUser;
    RequestPostProcessor mockAuthentication;
    

    
    
    @Before
    public void setUp() throws Exception  {
        
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .apply(springSecurity())
            .build();        
        
        currentUser = fixtures.user1;
        mockAuthentication = authenticate(currentUser);
    }
    
    @After
    public void tearDown() {
    } 
    
    
    Authentication makeAuthentication(BioDare2User user) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, user.getPassword(),USER_ROLES);
        authentication.setAuthenticated(true);
        return authentication;
    } 
    
    RequestPostProcessor authenticate(BioDare2User user) {
        return SecurityMockMvcRequestPostProcessors.authentication(makeAuthentication(user));
    }
    
    
}
