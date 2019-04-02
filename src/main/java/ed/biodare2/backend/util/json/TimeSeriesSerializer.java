/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class TimeSeriesSerializer extends StdSerializer<TimeSeries> {

    public TimeSeriesSerializer() {
        this(TimeSeries.class);
    }
   
    public TimeSeriesSerializer(Class<TimeSeries> t) {
        super(t);
    }
    
    @Override
    public void serialize(TimeSeries data, JsonGenerator jgen, SerializerProvider sp) throws IOException {
        
        jgen.writeStartObject();
        
        double[] times = new double[data.size()];
        double[] values = new double[data.size()];
        double[] stdErrs = new double[data.size()];
        double[] stdDevs = new double[data.size()];
        
        boolean hasStdErrs = false;
        boolean hasStdDevs = false;
        
        List<Timepoint> points = data.getTimepoints();
        for (int i = 0;i<points.size();i++) {
            Timepoint tp = points.get(i);
            times[i] = tp.getTime();
            values[i] = tp.getValue();
            if (tp.hasStdError()) {
                hasStdErrs = true;
                stdErrs[i] = tp.getStdError();
            } else {
                stdErrs[i] = Double.NaN;
            }
            if (tp.hasStdDev()) {
                hasStdDevs = true;
                stdDevs[i] = tp.getStdDev();
            } else {
                stdDevs[i] = Double.NaN;
            }
        }

        jgen.writeFieldName("times");
        jgen.writeArray(times, 0, times.length);

        jgen.writeFieldName("values");
        jgen.writeArray(values, 0, values.length);

        if (hasStdErrs) {
            jgen.writeFieldName("stderrs");
            jgen.writeArray(stdErrs, 0, stdErrs.length);            
        }
        if (hasStdDevs) {
            jgen.writeFieldName("stddevs");
            jgen.writeArray(stdDevs, 0, times.length);            
        }
        jgen.writeEndObject();        
    }
    
}
