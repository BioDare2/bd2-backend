/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class HourlyBinningRounder {
    
    
    public TimeSeries binToHour(TimeSeries serie) {
        TimeSeries binned = new TimeSeries();
        
        if (serie.isEmpty()) return binned;
        
        List<Timepoint> data = serie.getTimepoints();
        int bin = (int)Math.round(data.get(0).getTime());        
        double binValue = 0;
        int binSize = 0;
        double limit = bin+0.5;
        
        for (int i = 0; i< data.size(); i++) {
            Timepoint point = data.get(i);
            if (point.getTime() < limit) {
                binValue+=point.getValue();
                binSize++;
            } else {
                binned.add(bin, binValue/binSize);
                
                int newBin = (int)Math.round(point.getTime());        
                bin = newBin;
                limit = bin+0.5;
                binValue = point.getValue();
                binSize = 1;
            }
        }
        binned.add(bin, binValue/binSize);
        
        return binned;
    }


}
