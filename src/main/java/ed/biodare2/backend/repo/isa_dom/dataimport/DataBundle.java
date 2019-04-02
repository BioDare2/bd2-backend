/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.robust.dom.data.DetrendingType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class DataBundle implements Serializable {
   
    static final long serialVersionUID = 11L;
    
    public DetrendingType detrending;
    public List<DataTrace> backgrounds = new ArrayList<>();
    public List<DataTrace> data = new ArrayList<>();
    public List<DataBlock> blocks = new ArrayList<>();
    
}
