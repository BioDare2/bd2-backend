/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.ppa;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class PPASelectGroup {
    
    public long dataId;
    public String dataRef;
    public String label;
    public String selected;
    
    public boolean needsAttention;
    public boolean isIgnored;
    public boolean isCircadian;    
    
    public List<PPASelectItem> periods = new ArrayList<>();
    
}
