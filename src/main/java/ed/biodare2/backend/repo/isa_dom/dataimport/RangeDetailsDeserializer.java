/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 *
 * @author tzielins
 */
public class RangeDetailsDeserializer extends JsonDeserializer<RangeDetails> {

    static final String DataLabelK = "dataLabel";
    static final String TimeTypeK = "timeType";
    static final String FirstRowK = "firstRow";
    static final String ImgInterK = "imgInterval";
    static final String OffsetK = "timeOffset";
    
    @Override
    public RangeDetails deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        
        
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        
        if (node.has(DataLabelK)) 
            return deserializeDataProperties(node);
        
        if (node.has(TimeTypeK))
            return deserializeTimeProperties(node);

        return null;
        //throw new JsonParseException("Value is not a data nor time properties: "+node.toString(),jp.getCurrentLocation());
    }

    protected RangeDetails deserializeDataProperties(JsonNode node) {
        
        DataColumnProperties prop = new DataColumnProperties();
        prop.dataLabel = node.get(DataLabelK).asText();
        
        return prop;
    }

    protected RangeDetails deserializeTimeProperties(JsonNode node) {
        
        TimeColumnProperties prop = new TimeColumnProperties();
        //node.fieldNames().forEachRemaining( f -> System.out.println(f));
        
        //prop.timeType = TimeType.values()[node.get(TimeTypeK).intValue()];
        prop.timeType = TimeType.valueOf(node.get(TimeTypeK).asText());
        prop.firstRow = node.get(FirstRowK).intValue();
        if (node.has(OffsetK)) prop.timeOffset = node.get(OffsetK).asDouble();
        if (node.has(ImgInterK)) prop.imgInterval = node.get(ImgInterK).asDouble();
        return prop;
    }
    
}
