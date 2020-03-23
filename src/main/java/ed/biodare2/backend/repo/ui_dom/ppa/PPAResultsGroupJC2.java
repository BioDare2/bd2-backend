/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.ppa;

import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class PPAResultsGroupJC2 {
    
    public long rawDataId;
    public String label;
    public String dataRef;
    
    public List<PPASimpleResultEntry> results = new ArrayList<>();
}
