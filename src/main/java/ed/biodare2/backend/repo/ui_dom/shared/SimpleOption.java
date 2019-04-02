/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.shared;

/**
 *
 * @author tzielins
 */
public class SimpleOption {
    
    public String id;
    public String label;
    public boolean selected;

    public SimpleOption() {};
    
    public SimpleOption(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public SimpleOption(String id, String label,boolean selected) {
        this(id,label);
        this.selected = selected;
    }
    
    
}
