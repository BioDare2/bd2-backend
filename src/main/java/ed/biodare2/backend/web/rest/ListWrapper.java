/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.repo.ui_dom.shared.Page;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class ListWrapper<T> {
    
    public List<T> data;
    public Page currentPage;
    
    public ListWrapper() {
        this(new ArrayList<>());
    };
    
    public ListWrapper(List<T> data) {
        this(data, fullPage(data));
    }
    
    public ListWrapper(List<T> data, Page currentPage) {
        this.data = data;
        this.currentPage = currentPage;
    }
    
    public static Page fullPage(List<?> data) {
        return new Page(0, Math.max(data.size(),1), data.size());
    }
    
}
