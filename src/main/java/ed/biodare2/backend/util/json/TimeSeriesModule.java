/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import ed.robust.dom.data.TimeSeries;

/**
 *
 * @author Zielu
 */
public class TimeSeriesModule extends SimpleModule {
    
    private static final long serialVersionUID = 1L;

    public TimeSeriesModule()
    {
        //super(PackageVersion.VERSION); 
        addDeserializer(TimeSeries.class, new TimeSeriesDeSerializer());        
        addSerializer(TimeSeries.class, new TimeSeriesSerializer());
    }
    
}
