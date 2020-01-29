/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.tsdata;


import ed.biodare2.backend.repo.ui_dom.shared.Page;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class TraceSet {
    
    public String title;
    
    public List<Trace> traces = new ArrayList<>();
    
    public int totalTraces;
    
    public Page currentPage;
}
