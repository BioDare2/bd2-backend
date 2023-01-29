/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.mockito.Mockito.*;

/**
 *
 * @author Zielu
 */
public class CORSFilterTest {
    
    CORSFilter filter;
    
    public CORSFilterTest() {
    }
    
    @Before
    public void setUp() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(CORSFilter.CORS_ORIGINS_KEY, "localhost:3000,http://biodare.ed.ac.uk");
        
        filter = makeInstance(env);
    }
    
    @After
    public void tearDown() {
    }
    
    CORSFilter makeInstance(Environment env) {
        return new CORSFilter(env);
    }
    
    @Test
    public void setsCORSHeadersForAllowedOrigin() throws IOException, ServletException {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("http://localhost:9000/exp");
        request.addHeader("Origin", "http://localhost:3000/desc/index.html");
        
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        FilterChain chain = mock(FilterChain.class);
        
        filter.doFilter(request, resp, chain);
        
        assertEquals("http://localhost:3000",resp.getHeader("Access-Control-Allow-Origin"));
        assertEquals("true",resp.getHeader("Access-Control-Allow-Credentials"));
    }
    
    @Test
    public void setsAllowedAndExposedHeadersForAllowedOrigin() throws IOException, ServletException {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("http://localhost:9000/exp");
        request.addHeader("Origin", "http://localhost:3000/desc/index.html");
        
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        FilterChain chain = mock(FilterChain.class);
        
        filter.doFilter(request, resp, chain);
        
        String allowed = resp.getHeader("Access-Control-Allow-Headers");
        String[] expected = {"X-Requested-With","Content-Type","Access-Control-Request-Method","Access-Control-Request-Headers","Authorization","X-XSRF-TOKEN","X-Auth-Token"};
        for (String s : expected)
            assertTrue(s+" missing in allowed header",allowed.contains(s));
        
        String exposed =  resp.getHeader("Access-Control-Expose-Headers");
        expected = new String[]{"X-XSRF-TOKEN,x-auth-token"};
        for (String s : expected)
            assertTrue(s+" missing in exposed header",exposed.contains(s));
    }    

    @Test
    public void extractAllowedOriginsExpandsEnvParameter() {
        
        MockEnvironment env = new MockEnvironment();
        env.setProperty(CORSFilter.CORS_ORIGINS_KEY, "localhost:3000,http://biodare.ed.ac.uk");

        Set<String> res = new HashSet<>(filter.extractAllowedOrigins(env));
        Set<String> exp = new HashSet<>(Arrays.asList("http://localhost:3000","https://localhost:3000","http://biodare.ed.ac.uk"));
        
        assertEquals(exp,res);
    }
    
    @Test
    public void allowedOriginReturnsServerRootForCorrectOrigin() {
    
        //filter.origins = Arrays.asList("http://localhost:3000","https://localhost:3000","http://biodare.ed.ac.uk");
        
        String origin = "http://localhost:3000/exp/index.html";        
        String res = filter.allowedOrigin(origin);
        String exp = "http://localhost:3000";
        
        assertEquals(exp,res);
        
        origin = "http://biodare.ed.ac.uk";        
        res = filter.allowedOrigin(origin);
        exp = "http://biodare.ed.ac.uk";
        
        assertEquals(exp,res);
        
        origin = "https://biodare.ed.ac.uk";        
        res = filter.allowedOrigin(origin);
        
        assertNull(res);
    }

    @Test
    public void allowedOriginReturnsNullForEmptyOrigin() {
    
        filter.origins = Arrays.asList("http://localhost:3000","https://localhost:3000","http://biodare.ed.ac.uk");
        
        String origin = null;        
        String res = filter.allowedOrigin(origin);
        assertNull(res);
        
        origin = "";
        res = filter.allowedOrigin(origin);
        assertNull(res);
        
        origin = "http://";
        res = filter.allowedOrigin(origin);
        assertNull(res);
        
    }    
}
