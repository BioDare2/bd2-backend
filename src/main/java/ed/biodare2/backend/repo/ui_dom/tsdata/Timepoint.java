/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.tsdata;

/**
 *
 * @author Zielu
 */
public class Timepoint {
    
    public double x;
    public double y;
    
    public Timepoint() {};

    public Timepoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Timepoint(ed.robust.dom.data.Timepoint point) {
        this(point.getTime(), point.getValue());
    }
    
}
