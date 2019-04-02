/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class TimeSeriesDeSerializer extends StdDeserializer<TimeSeries> {

    public TimeSeriesDeSerializer() {
        this(TimeSeries.class);
    }
   
    public TimeSeriesDeSerializer(Class<TimeSeries> t) {
        super(t);
    }

    @Override
    public TimeSeries deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        
        //System.out.println("\n\n\nCustom TS deserialization called");
        JsonNode node = jp.getCodec().readTree(jp);
        
        JsonNode tN = node.get("times");
        List<Double> times = new ArrayList<>();
        for (JsonNode val : tN) {
            times.add(val.asDouble());
        }

        JsonNode vN = node.get("values");
        List<Double> values = new ArrayList<>();
        for (JsonNode val : vN) {
            values.add(val.asDouble());
        }
        
        List<Double> stderrs = new ArrayList<>();
        if (node.has("stderrs")) {
            for (JsonNode e : node.get("stderrs")) {
                double d = e.asDouble();
                stderrs.add(Double.isNaN(d) ? null : d);
            }            
        } else {
            for (Double t : times) {
                stderrs.add(null);
            }
        }
        
        List<Double> stddevs = new ArrayList<>();
        if (node.has("stddevs")) {
            for (JsonNode e : node.get("stddevs")) {
                double d = e.asDouble();
                stddevs.add(Double.isNaN(d) ? null : d);
            }            
        } else {
            for (Double t : times) {
                stddevs.add(null);
            }
        }
        
        
        TimeSeries data = new TimeSeries();
        for (int i = 0;i<times.size();i++) {
            data.add(new Timepoint(times.get(i),values.get(i),stderrs.get(i), stddevs.get(i)));
        }
        return data;
    }
    

    
}
