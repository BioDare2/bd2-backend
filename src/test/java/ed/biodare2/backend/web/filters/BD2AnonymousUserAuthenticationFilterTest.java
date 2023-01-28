/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.SecurityContextRepository;
/**
 *
 * @author tzielins
 */
public class BD2AnonymousUserAuthenticationFilterTest {
    
    public BD2AnonymousUserAuthenticationFilterTest() {
    }
    
    BD2AnonymousUserAuthenticationFilter instance;
    AuthenticationEventPublisher eventPublisher;
    SecurityContextRepository securityContextRepository;
    
    @Before
    public void init() {
        eventPublisher = mock(AuthenticationEventPublisher.class);
        securityContextRepository = mock(SecurityContextRepository.class);
        instance = new BD2AnonymousUserAuthenticationFilter(eventPublisher, securityContextRepository);
    }

    @Test
    public void makeUserCratesAnonymouseUser() {
        
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS","ROLE_USER");
        BioDare2User user = instance.makeUser("Ktos", "biodare.ed.ac.uk", authorities);

        assertTrue(user.isAnonymous());
        assertEquals(authorities,user.getAuthorities());
        
    }
    
    @Test
    public void createAuthenticationCreatesAnononymousAuthenticationWithRemoteAddress() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        BD2AnonymousUserAuthenticationToken token = instance.createAuthentication(request);
        assertEquals("bla.bla.bla",token.remote);
        assertTrue(token.getUser().isAnonymous());
    
    }
    
    @Test
    public void createAuthenticationCreatesAnononymousAuthenticationWithWebDetails() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        BD2AnonymousUserAuthenticationToken token = instance.createAuthentication(request);
        assertEquals("bla.bla.bla",token.remote);
        assertTrue(token.getUser().isAnonymous());
        assertNotNull(token.getDetails());
        assertTrue(token.getDetails() instanceof WebAuthenticationDetails );
    
    } 
    
    @Test
    public void createAuthenticationCreatesAnononymousAuthenticationWithControlRDMAspect() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        BD2AnonymousUserAuthenticationToken token = instance.createAuthentication(request);
        
        UserAccount ua = (UserAccount)token.getUser();
        assertNotNull(ua.getRdmAspect());
        assertEquals(RDMCohort.CONTROL,ua.getRdmAspect().getCohort());
    
    }
    

    @Test
    public void createAuthenticationCreatesAnononymousAuthenticationWithFreeSubscription() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        BD2AnonymousUserAuthenticationToken token = instance.createAuthentication(request);
        
        UserAccount ua = (UserAccount)token.getUser();
        assertNotNull(ua.getSubscription());
        assertEquals(SubscriptionType.FREE,ua.getSubscription().getKind());
    
    }
    
    @Test
    public void createAuthenticationCreatesAnononymousAuthenticationWithLockedCorrectRoles() {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        BD2AnonymousUserAuthenticationToken token = instance.createAuthentication(request);
        
        try {
            token.getAuthorities().addAll(AuthorityUtils.createAuthorityList("ROLE_X"));
            fail("Exceptin expected");            
        } catch (UnsupportedOperationException e) {};

        try {
            token.getUser().getAuthorities().addAll(AuthorityUtils.createAuthorityList("ROLE_X"));
            fail("Exceptin expected");            
        } catch (UnsupportedOperationException e) {};
        
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS","ROLE_READER","ROLE_USER");
        assertEquals(roles,token.getAuthorities());
        assertEquals(roles,token.getUser().getAuthorities());
    
    }    
    
    @Test
    public void doFilterAddsNewAnonymouseAuthenticationToTheEmptyContext() throws IOException, ServletException {
        
        SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.getContext().setAuthentication(null);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        MockHttpServletResponse resp = new MockHttpServletResponse();        
        FilterChain chain = mock(FilterChain.class);
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        instance.doFilter(request, resp, chain);
        
        BD2AnonymousUserAuthenticationToken auth = (BD2AnonymousUserAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        
        assertSame(auth.getPrincipal(),auth.getUser());
        assertTrue(auth.user.isAnonymous());
        assertTrue(auth.user.getLogin().startsWith(instance.PREFIX));
        
        verify(chain).doFilter(request, resp);
        
    }
    
    @Test
    public void doFilterLeavesExistingAuthenticationIntact() throws IOException, ServletException {
        
        SecurityContextHolder.createEmptyContext();
        Authentication auth = mock(Authentication.class);
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("bla.bla.bla");
        
        MockHttpServletResponse resp = new MockHttpServletResponse();        
        FilterChain chain = mock(FilterChain.class);
        
        instance.doFilter(request, resp, chain);
        
        Authentication auth2 = SecurityContextHolder.getContext().getAuthentication();
        assertSame(auth,auth2);
    }    
    
}
