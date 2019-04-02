/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web;

import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.web.filters.BD2AnonymousUserAuthenticationFilter;
import ed.biodare2.backend.web.filters.RefreshUserFilter;
import ed.biodare2.backend.web.listeners.OKLogoutSuccessHandler;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
//@EnableWebSecurity
public class WebSecurityConfigurationOld /*extends WebSecurityConfigurerAdapter*/ {
    
    
    
    //@Configuration
    @Order(1)                                                        
    public static class ServicesBackendWebSecurityConfigurationAdapter /*extends WebSecurityConfigurerAdapter*/ {
 
        final List<GrantedAuthority> roles = Collections.unmodifiableList(
                AuthorityUtils.createAuthorityList("ROLE_SERVICE", "ROLE_WRITER"));
        
        protected void configure(HttpSecurity http) throws Exception {

            http
                .antMatcher("/api/services/**")                               
                .authorizeRequests()
                    .anyRequest().hasRole("SERVICE")
                    .and()
                .anonymous().disable()
                .csrf().disable()
                .httpBasic();                    
        }

        /*
        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            
            System.out.println("Config AUTH MANAGER");
            auth
                .inMemoryAuthentication()
                    .withUser("ppaserver").password("285TuBu..S").roles("SERVICE","WRITER");
        }   
        
        */
    }

    
    //@Configuration
    @Order(2)                                                        
    public static class UIBackendWebSecurityConfigurationAdapter /*extends WebSecurityConfigurerAdapter*/ {

        final List<GrantedAuthority> roles = Collections.unmodifiableList(
                AuthorityUtils.createAuthorityList("ROLE_READER","ROLE_USER","ROLE_WRITER"));
        
        //@Autowired
        //@Qualifier("BackendServiceUsers")
        //List<UserDetails> backendServiceUsers;
        
        @Autowired
        PasswordEncoder passwordEncoder;        
        
        @Autowired
        UserAccountRep accounts;
        
        @Autowired
        AuthenticationEventPublisher eventPublisher;
        
        BD2AnonymousUserAuthenticationFilter defaultUserFilter() {
            return new BD2AnonymousUserAuthenticationFilter(eventPublisher);
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
        
        //@Override
        protected void configure(HttpSecurity http) throws Exception {


            http
                //.headers().frameOptions().sameOrigin().and()    //enable for h2 console
                .authorizeRequests()
                    .antMatchers("/", "/home","node_modules").permitAll()
                    .antMatchers("browser-sync").denyAll()
                    .anyRequest().hasRole("USER")//.authenticated()
                    .and()
                .anonymous().authenticationFilter(defaultUserFilter()).and()                
                //.addFilterBefore(basicLoginFilter(), AnonymousAuthenticationFilter.class) //disabled now to use spring one, may be necessary for better session handling
                //.addFilterBefore(defaultUserFilter(), AnonymousAuthenticationFilter.class)                    
                .sessionManagement().sessionFixation().newSession().and()
                .csrf().disable()
                /*.formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()*/
                .httpBasic().and()
                .addFilterAfter(refreshUserFilter(), BasicAuthenticationFilter.class)
                .logout().logoutSuccessHandler(new OKLogoutSuccessHandler())
                         .logoutRequestMatcher(new AntPathRequestMatcher("/**/logout"))
                    .permitAll();
        }

        //@Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authProvider());           
            //auth.userDetailsService(userDetailsService()); 
        }    
        
        @Bean javax.servlet.Filter refreshUserFilter() {
            return new RefreshUserFilter(accounts);
        }
        
        @Bean
        public DaoAuthenticationProvider authProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService());
            authProvider.setPasswordEncoder(passwordEncoder);
            return authProvider;
        }        

        @Bean
        //@Override
        protected UserDetailsService userDetailsService() {

            final UserAccount ppa;        
            ppa = new UserAccount();
            ppa.setLogin("ppaserver");
            ppa.setFirstName("ppaserver");
            ppa.setLastName("ppaserver");
            ppa.setEmail("biodare@ed.ac.uk");
            ppa.setPassword(passwordEncoder.encode("285TuBu..S"));
            ppa.setSystem(true);
            ppa.setBackendOnly(true);
            ppa.setInstitution("BioDare");
            ppa.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_SERVICE"));
            

            return (username) ->  {
                // System.out.println("Second user service for: "+username);
                
                if (username.equals("ppaserver")) {
                    return ppa;
                }
                return accounts.findByLogin(username)
                                    .map( a -> {
                                            a.setAuthorities(roles);
                                            return a;
                                            })
                                    .orElseThrow(() -> new UsernameNotFoundException("could not find the user '"+ username + "'"));                            
            };
        }        
        
    }
    

}