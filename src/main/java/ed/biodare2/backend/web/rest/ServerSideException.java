/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author tzielins
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerSideException extends WebMappedException {

    public ServerSideException(String msg) {
            super(msg);
    }   
    
    public ServerSideException(String msg,Throwable t) {
            super(msg,t);
    }
    
    public ServerSideException(Throwable t) {
            super(t);
    }      
}
