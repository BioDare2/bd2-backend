/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author tzielins
 */
public class RangeDetailsDeserializer extends StdDeserializer<RangeDetails> {

    static final String DataLabelK = "dataLabel";
    static final String TimeTypeK = "timeType";
    static final String FirstRowK = "firstRow";
    static final String ImgInterK = "imgInterval";
    static final String OffsetK = "timeOffset";

    public RangeDetailsDeserializer() {
	super(RangeDetails.class);
    }
    
    @Override
    public RangeDetails deserialize(JsonParser jp, DeserializationContext dc) throws JacksonException {
        
        JsonNode node = jp.readValueAsTree();
        
        if (node.has(DataLabelK)) 
            return deserializeDataProperties(node);
        
        if (node.has(TimeTypeK))
            return deserializeTimeProperties(node);

        return null;
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
