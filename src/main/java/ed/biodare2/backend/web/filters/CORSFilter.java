/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author tzielins
 */
public class CORSFilter implements Filter {
    
    public static final String CORS_ORIGINS_KEY = "bd2.cors.origins";
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    List<String> origins = new ArrayList<>();
    final boolean DEBUG = false;
    final boolean DEBUG_HEADERS = false;

    public CORSFilter(Environment env) {
        this.origins = extractAllowedOrigins(env);
        log.info(this.getClass().getSimpleName()+" created");
    }
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
        
        log.info(this.getClass().getSimpleName()+" initialized");
        log.info("CORS Allowed Origins: "+origins);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                                FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String method = request.getMethod();
        
        String origin = request.getHeader("Origin");
        
        //log.warn("A "+request.getHeader("Authorization"));
        if (DEBUG_HEADERS) {
            log.debug("HEADERS for {}",request.getRequestURI());
            Collections.list(request.getHeaderNames()).forEach( header -> log.debug("{}: {}",header,request.getHeader(header)));
            log.debug("HEADERS END");
            if (request.getCookies() != null) {
                log.debug("COOKIES");
                Arrays.stream(request.getCookies()).forEach( cookie -> log.debug("{}: {}",cookie.getName(),cookie.getValue()));
                log.debug("COOKIES end");
            }
            log.debug("\n");
        }//*/
        
        String allowed = allowedOrigin(origin);
        if (DEBUG) log.debug("CORSFilter for: "+origin+"; allowed: "+allowed);
        
        if (allowed != null) {

            response.setHeader("Access-Control-Allow-Origin", allowed);
            response.setHeader("Access-Control-Allow-Methods",
                        "PUT,POST,GET,OPTIONS,DELETE");
            response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
            //needed so JS applications can send session cookies with requests
            response.setHeader("Access-Control-Allow-Credentials", "true");
            //what headers can come with request
            response.setHeader(
                        "Access-Control-Allow-Headers",
                        "Origin,Accept,X-Requested-With,Content-Type,Content-Disposition,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,X-XSRF-TOKEN,X-Auth-Token");
            //tells which headers can be visible in JS
            response.setHeader(
                        "Access-Control-Expose-Headers",
                        "X-XSRF-TOKEN,x-auth-token");
           
        };
       
        
        if ("OPTIONS".equals(method)) {
            response.setStatus(HttpStatus.OK.value());
        }
        else {
            chain.doFilter(req, res);
        }
    }
    
    @Override
    public void destroy() {
        log.info(this.getClass().getSimpleName()+" destroyed");
    }

    protected String allowedOrigin(String origin) {
        if (origin == null) return null;
        for (String allowed: origins) {
            if (origin.startsWith(allowed)) return allowed;
        }
        return null;
    }

    protected List<String> extractAllowedOrigins(Environment env) {
        
        List<String> allowed = new ArrayList<>();
        if (!env.containsProperty(CORS_ORIGINS_KEY)) return allowed;
        

        String names = env.getProperty(CORS_ORIGINS_KEY);        
        for (String name : names.split(",")) {
            if (name.startsWith("http")) allowed.add(name);
            else {
                allowed.add("http://"+name);
                allowed.add("https://"+name);
            }
        }
        return allowed;
    }
}
