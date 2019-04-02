/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class ListWrapper<T> {
    
    public List<T> data;
    
    public ListWrapper() {
        this(new ArrayList<>());
    };
    
    public ListWrapper(List<T> data) {
        this.data = data;
    }
    
}
