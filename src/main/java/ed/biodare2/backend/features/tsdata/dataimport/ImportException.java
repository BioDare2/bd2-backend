/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

/**
 *
 * @author tzielins
 */
public class ImportException extends Exception {
    
    public ImportException(String msg) {
        super(msg);
    }
    
    public ImportException(String msg,Throwable err) {
        super(msg,err);
    }
}
