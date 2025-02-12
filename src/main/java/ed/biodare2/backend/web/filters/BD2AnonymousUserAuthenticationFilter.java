/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.features.rdmsocial.RDMUserAspect;
import ed.biodare2.backend.features.subscriptions.AccountSubscription;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.web.tracking.SecurityTracker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.GenericFilterBean;

//public class BD2AnonymousUserAuthenticationFilter extends GenericFilterBean implements
public class BD2AnonymousUserAuthenticationFilter extends AnonymousAuthenticationFilter implements
		InitializingBean {


        private final List<GrantedAuthority> authorities;
        private final static AtomicLong IDS = new AtomicLong(1);
        private final AuthenticationEventPublisher eventPublisher;
        //private final SecurityTracker tracker = new SecurityTracker();
        private SecurityContextRepository securityContextRepository;
        
        protected String PREFIX = "ANONYM_";
        boolean SpringDebug = false;
        
	public BD2AnonymousUserAuthenticationFilter(AuthenticationEventPublisher eventPublisher, SecurityContextRepository securityContextRepository) {
            super("/**","anonymousUser",Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS","ROLE_READER","ROLE_USER")));
            this.authorities = Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS","ROLE_READER","ROLE_USER"));
            this.eventPublisher = eventPublisher;
            this.securityContextRepository = securityContextRepository;
            
            logger.debug(this.getClass().getSimpleName()+" created");
            SpringDebug = LoggerFactory.getLogger(GenericFilterBean.class).isDebugEnabled();
	}
        
	public BD2AnonymousUserAuthenticationFilter(AuthenticationEventPublisher eventPublisher) {
            this(eventPublisher, new HttpSessionSecurityContextRepository());
        }
        

    public void setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
                     {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            BD2AnonymousUserAuthenticationToken authentication = createAuthentication((HttpServletRequest) req);

            SecurityContextHolder.getContext().setAuthentication(authentication);            
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), (HttpServletRequest)req,(HttpServletResponse) res);
            
            eventPublisher.publishAuthenticationSuccess(authentication);

            // Check for X-Forwarded-For header
            String remote = ((HttpServletRequest) req).getHeader("X-Forwarded-For");
            if (remote == null || remote.isEmpty()) {
                remote = req.getRemoteAddr();
            } else {
                // X-Forwarded-For can contain multiple IPs, take the first one
                remote = remote.split(",")[0].trim();
            }
            
            //tracker.anonymous(authentication,(HttpServletRequest) req);

            if (logger.isInfoEnabled()) {
                logger.info("DefaultUser created: "+authentication.getUser().getLogin()+" for: "+ remote);
            }

            if (SpringDebug) {
                    logger.debug("Populated SecurityContextHolder with default user: '"
                                    + SecurityContextHolder.getContext().getAuthentication() + "'");
            }
        }
        else {
                if (SpringDebug) {
                        logger.debug("SecurityContextHolder not populated with default token, as it already contained: '"
                                        + SecurityContextHolder.getContext().getAuthentication() + "'");
                }
        }

        chain.doFilter(req, res);
    }

        @Override
    protected BD2AnonymousUserAuthenticationToken createAuthentication(HttpServletRequest request) {

        String userName = PREFIX+IDS.getAndIncrement();
        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
        
        // Check for X-Forwarded-For header
        String remote = request.getHeader("X-Forwarded-For");
        if (remote == null || remote.isEmpty()) {
            remote = details.getRemoteAddress();
        } else {
            // X-Forwarded-For can contain multiple IPs, take the first one
            remote = remote.split(",")[0].trim();
        }

        BioDare2User principal = makeUser(userName,remote,authorities);

        BD2AnonymousUserAuthenticationToken auth = new BD2AnonymousUserAuthenticationToken(principal, details, authorities);

        //String details = userName+":"+remote;
        //auth.setDetails(details);

        return auth;
    }

        @Override
    public List<GrantedAuthority> getAuthorities() {
            return authorities;
    }

        
    protected BioDare2User makeUser(String userName, String remote, List<GrantedAuthority> authorities) {
        
        UserAccount account = new UserAccount();
        account.setLogin(userName);
        account.setPassword("");
        account.setFirstName("Anonymous");
        account.setLastName("User");
        account.setEmail("anonymous@biodare.ed.ac.uk");
        account.setAnonymous(true);
        account.setAuthorities(authorities);
        account.setSupervisor(account);
        
        AccountSubscription sub = new AccountSubscription();
        sub.setKind(SubscriptionType.FREE);
        sub.setRenewDate(LocalDate.now().plusYears(1));
        account.setSubscription(sub);
        
        RDMUserAspect aspect = new RDMUserAspect();
        aspect.setCohort(RDMCohort.CONTROL);
        account.setRdmAspect(aspect);
        
        return account;
    }
}