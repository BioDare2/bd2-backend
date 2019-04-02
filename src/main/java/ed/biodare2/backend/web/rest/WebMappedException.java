/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

/**
 *
 * @author Zielu
 */
public class WebMappedException extends RuntimeException {
    
    
    public WebMappedException(String msg) {
        super(msg);
    }
    
    public WebMappedException(String msg,Throwable e) {
        super(msg,e);
    }
    
    public WebMappedException(Throwable e) {
        super(e);
    }
}
