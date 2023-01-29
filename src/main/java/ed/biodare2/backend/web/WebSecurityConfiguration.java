/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web;

import static ed.biodare2.BioDare2WSApplication.DEBUG_WEB;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.web.filters.BD2AnonymousUserAuthenticationFilter;
import ed.biodare2.backend.web.filters.CORSFilter;
import ed.biodare2.backend.web.filters.MonitoringFilter;
import ed.biodare2.backend.web.filters.RefreshUserFilter;
import ed.biodare2.backend.web.listeners.OKLogoutSuccessHandler;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 *
 * @author tzielins
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    
    
    // CORS
    @Bean
    FilterRegistrationBean corsFilter(Environment env) {
        FilterRegistrationBean reg = new FilterRegistrationBean(
                new CORSFilter(env)
        );
        reg.setOrder(-100);
        return reg;
    }
    
    @Bean
    FilterRegistrationBean monitoringFilter() {
        FilterRegistrationBean reg = new FilterRegistrationBean(
                new MonitoringFilter(DEBUG_WEB)
        );
        reg.setOrder(-200);
        return reg;
    }    
    
    
    @Bean("ppaUsername")
    public String ppaUsername() {
        return "ppaserver";
    }
    
    @Bean("ppaPassword")
    public String ppaPassword() {
        
        final String chars = "abcdefghijklmnopqrstuvwxABCDEFGHIJKLMOPRSTUVWXZ12345690";
        char[] pass = new char[12];
        
        for (int i = 0; i< pass.length; i++) {
            int x = (int) (Math.random()*chars.length());
            pass[i] = chars.charAt(x);
        }
        
        String password = String.valueOf(pass);                
        // System.out.println("PASS: "+password);
        return password;
    }
    
    
    @Bean("ppaPasswordEncoded")
    public String ppaPasswordEncoded(String ppaPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(ppaPassword);
    }

    protected UserDetails makeUser(String username, String ppaPasswordEncoded, String... roles) {
        
        // System.out.println(ppaPassword+" En:"+passwordEncoder.getClass().getName());
        return User.builder()
                    .username(username)
                    .password(ppaPasswordEncoded)
                    .roles(roles).build();
    }
    
    @Bean
    protected UserDetailsService userDetailsService(
            final String ppaUsername,
            final String ppaPasswordEncoded, 
            final UserAccountRep accounts) {

        final List<GrantedAuthority> roles = Collections.unmodifiableList(
            AuthorityUtils.createAuthorityList("ROLE_READER","ROLE_USER","ROLE_WRITER"));

        return (username) ->  {
            // System.out.println("Custom user service for: "+username);

            if (ppaUsername.equals(username)) {
                // System.out.println("PP pass: "+ppaUser.getPassword());
                // had to make new for each call, there was some issue with filters
                return makeUser(ppaUsername, ppaPasswordEncoded, "SERVICE");
            }
            
            final UserAccount acc = accounts.findByLogin(username)
                    .map( a -> {
                        a.setAuthorities(roles);
                        return a;
                    })
                    .orElseThrow(() -> new UsernameNotFoundException("could not find the user '"+ username + "'"));                            

            return acc;

        };
    }        

    
    /*
    @Configuration
    @Order(1)                                                        
    public static class ServicesBackendWebSecurityConfiguration {
 
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            http
                .securityMatcher("/api/services/**")                               
                .authorizeHttpRequests()
                    .anyRequest().hasRole("SERVICE")
                    .and()
                .anonymous().disable()
                .csrf().disable()
                .httpBasic();
            return http.build();
        }

    }
    */
    
    @Configuration
    @Order(2)                                                        
    public static class UIBackendWebSecurityConfiguration {

        
        @Autowired
        UserAccountRep accounts;
        
        @Autowired
        AuthenticationEventPublisher eventPublisher;
        
        //@Autowired
        //SecurityContextRepository securityContextRepository;
        
        BD2AnonymousUserAuthenticationFilter defaultUserFilter() {
            return new BD2AnonymousUserAuthenticationFilter(eventPublisher);
        }  
        
        RefreshUserFilter refreshUserFilter() {
            return new RefreshUserFilter(accounts);
        }        
        
        /* Disabled for a moment, as standard implementation seems to be working and it handles the angular x-requested with
        CustomBasicAuthenticationFilter basicLoginFilter() throws Exception {

            CustomBasicAuthenticationFilter filter = new CustomBasicAuthenticationFilter(authenticationManager());
            /*SessionAuthenticationStrategy sessionStrategy = new SessionFixationProtectionStrategy();
            AuthenticationEntryPoint entry = new BasicAuthenticationEntryPoint();
            CustomBasicAuthenticationFilter filter = new CustomBasicAuthenticationFilter(authenticationManager(),entry);
            filter.setSessionAuthenticationStrategy(sessionStrategy);*/
            /*return filter;
        }*/
        
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            // that is the spring6 default context repo which here is created explicitrly so
            // I can pass it to the http.basic filter. 
            // otherwise it does not save the user to the session and basic login from angular http header does not work
            // https://github.com/spring-projects/spring-security/issues/12431
            SecurityContextRepository securityContextRepository = new DelegatingSecurityContextRepository(
				new RequestAttributeSecurityContextRepository(),
				new HttpSessionSecurityContextRepository()
			);
        
            http.securityContext((securityContext) -> securityContext
			.securityContextRepository(securityContextRepository)
                        .requireExplicitSave(true)
            );
            
            http
                //.headers().frameOptions().sameOrigin().and()    //enable for h2 console
                .authorizeHttpRequests()
                    .requestMatchers("/", "/home","node_modules").permitAll()
                    .requestMatchers("browser-sync").denyAll()
                    .anyRequest().hasRole("USER")//.authenticated()
                    .and()
                .anonymous().authenticationFilter(defaultUserFilter()).and()                
                //.addFilterBefore(basicLoginFilter(), AnonymousAuthenticationFilter.class) //disabled now to use spring one, may be necessary for better session handling
                //.addFilterBefore(defaultUserFilter(), AnonymousAuthenticationFilter.class)  changeSessionId                  
                //.sessionManagement().sessionFixation().newSession().and() changed as it may be better
                .sessionManagement().sessionFixation().changeSessionId().and()                    
                .csrf().disable()
                // all this fluff is needed to pass the security context to authentication filter, basic filter is aparentrly made to be stateless
                .httpBasic((basic) -> basic.addObjectPostProcessor(new ObjectPostProcessor<BasicAuthenticationFilter>() {
                        public BasicAuthenticationFilter postProcess(BasicAuthenticationFilter filter) {
                            filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
                        return filter;
                        }
                }))/*.and()*/
                .addFilterAfter(refreshUserFilter(), BasicAuthenticationFilter.class)
                .logout().logoutSuccessHandler(new OKLogoutSuccessHandler())
                         .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout"))
                         .permitAll()
                    ;
            


            
            return http.build();
        }
        
    }
     
}
