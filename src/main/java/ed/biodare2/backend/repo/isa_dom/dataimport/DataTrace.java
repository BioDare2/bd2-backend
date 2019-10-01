/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.robust.dom.data.TimeSeries;
import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class DataTrace implements Cloneable, Serializable {
   
    static final long serialVersionUID = 11L;
    
    public String container;
    public CellCoordinates coordinates;
    public int traceNr;
    public long dataId;
    public long rawDataId;
            
    public String traceRef;
    public String traceFullRef;
    
    public CellRole role;
    public DataColumnProperties details;
    
    public TimeSeries trace;
    
    @Override
    public DataTrace clone() {
        try {
            return (DataTrace)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Unexpected: "+e.getMessage(),e);
        }
    }
}
