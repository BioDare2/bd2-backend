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
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FormatException extends WebMappedException {

    public FormatException(String msg) {
            super(msg);
    }    
    
    public FormatException(String msg,Throwable e) {
            super(msg,e);
    }   
    
    public FormatException(Throwable e) {
            super(e);
    }     
}
