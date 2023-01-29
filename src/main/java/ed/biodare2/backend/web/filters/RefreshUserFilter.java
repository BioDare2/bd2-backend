/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author tzielins
 */
public class RefreshUserFilter extends GenericFilterBean {

    final UserAccountRep accounts;
    
    @Autowired
    public RefreshUserFilter(UserAccountRep accounts) {
        this.accounts = accounts;
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {

            BioDare2User user = extractForRefresh(authentication);
            
            if (user != null) {
                logger.debug("Refreshing session user: "+user.getLogin());
                
                Optional<UserAccount> freshUserO = accounts.findById(user.getId());
                if (freshUserO.isPresent()) {

                    Authentication freshAuthentication = makeAuthentication(freshUserO.get(),authentication,req);
                    
                    SecurityContextHolder.getContext().setAuthentication(freshAuthentication);
                    logger.info("Refreshed user in session: "+user.getLogin());
                } else {
                    logger.warn("Tried to refresh user "+user.getLogin()+" but it was not found");
                }
            }
            

        }
       
        fc.doFilter(req, res);
    }

    protected BioDare2User extractForRefresh(Authentication authentication) {
        
        Object principal = authentication.getPrincipal();
        if (principal == null) return null;
        if (principal instanceof BioDare2User) {
            BioDare2User user = (BioDare2User)principal;
            if (!user.isAnonymous() && user.hasDirtySession()) return user;
        }
        return null;
    }

    protected Authentication makeAuthentication(UserAccount freshUser, Authentication oldAuth, ServletRequest req) {
        
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(freshUser, oldAuth.getCredentials(),oldAuth.getAuthorities());
        return token;
    }


    
}
