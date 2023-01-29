/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class MonitoringFilter implements Filter {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    boolean DEBUG = true;
    boolean DEBUG_HEADERS = true;
    boolean DEBUG_COOKIES = true;
    boolean DEBUG_RESPONSE = true;

    public MonitoringFilter(boolean debug) {
        this.DEBUG = debug;
    }
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.info(this.getClass().getSimpleName()+" initialized");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        if (!DEBUG) {
            chain.doFilter(req, resp);
        } else {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) resp;
                
                log.debug("REQUEST {}",request.getRequestURI());
                if (DEBUG_HEADERS) {
                    log.debug("HEADERS",request.getRequestURI());
                    Collections.list(request.getHeaderNames()).forEach( header -> log.debug("{}: {}",header,request.getHeader(header)));
                    log.debug("HEADERS END");
                }
                if (DEBUG_COOKIES) {
                    
                    if (request.getCookies() != null) {
                        log.debug("COOKIES");
                        Arrays.stream(request.getCookies()).forEach( cookie -> log.debug("{}: {}",cookie.getName(),cookie.getValue()));
                        log.debug("COOKIES END");
                    } else {
                        log.debug("NO COOKIES");
                    }
                }
                chain.doFilter(req, resp);
                
                if (DEBUG_RESPONSE) {
                    log.debug("RESPONSE FOR {} {}",request.getRequestURI(),response.getStatus());
                    log.debug("CONTENT TYPE {}",response.getContentType());
                    response.getHeaderNames().forEach( header -> log.debug("{}: {}",header,response.getHeader(header)));
                    
                  
                    log.debug("RESPONSE END");
                }
                
        }
    }

    @Override
    public void destroy() {
        log.info(this.getClass().getSimpleName()+" destroyed");
    }
    
}
