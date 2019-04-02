/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author tzielins
 */
public class RefreshUserFilterTest {
    
    UserAccountRep accounts;
    RefreshUserFilter filter;
    public RefreshUserFilterTest() {
    }
    
    @Before
    public void setUp() {
        
        accounts = mock(UserAccountRep.class);
        filter = new RefreshUserFilter(accounts);
    }
    
    

    @Test
    public void makeAuthenticationUsesExistingCredentialsAndRoles() {
        
        String user = "ala";
        String credentials = "pass";
        Collection<? extends GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS","ROLE_READER");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,credentials,roles);
        
        UserAccount u = UserAccount.testInstance(1);
        Authentication resp = filter.makeAuthentication(u, auth, null);
        
        assertSame(u,resp.getPrincipal());
        assertSame(credentials,resp.getCredentials());
        assertEquals(roles,resp.getAuthorities());
        assertTrue(resp.isAuthenticated());
            
    }
    
    @Test
    public void extractForResfreshGivesNullForNullPrincipaOrAnonymous() {
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null,"");
        
        BioDare2User user = filter.extractForRefresh(auth);
        assertNull(user);
        
        UserAccount u = UserAccount.testInstance(1);
        u.setAnonymous(true);
        auth = new UsernamePasswordAuthenticationToken(null,"");
        
        user = filter.extractForRefresh(auth);
        assertNull(user);        
        
    }
    
    @Test
    public void extractForResfreshGivesUsersIfDirtySession() {
        
        
        UserAccount u = UserAccount.testInstance(1);
        u.setDirtySession(true);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u,"");
        
        BioDare2User user = filter.extractForRefresh(auth);
        assertSame(u,user);        
        
    }  
    
    @Test
    public void extractForResfreshGivesNullIfSessionClean() {
        
        
        UserAccount u = UserAccount.testInstance(1);
        u.setDirtySession(false);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u,"");
        
        BioDare2User user = filter.extractForRefresh(auth);
        assertNull(user);        
        
    }     
    
    @Test
    public void doFilterChangesAuthentication() throws Exception {
        
        ServletRequest req = mock(ServletRequest.class);
        ServletResponse res = mock(ServletResponse.class);
        FilterChain fc = mock(FilterChain.class);
        
        UserAccount u = UserAccount.testInstance(1);
        u.setDirtySession(true);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u,"");
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        UserAccount n = UserAccount.testInstance(2);
        when(accounts.findById(eq(1L))).thenReturn(Optional.of(n));
        
        filter.doFilter(req, res, fc);
        verify(fc).doFilter(eq(req), eq(res));
        assertNotSame(auth,SecurityContextHolder.getContext().getAuthentication());
        assertSame(n,SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
            
    
}
