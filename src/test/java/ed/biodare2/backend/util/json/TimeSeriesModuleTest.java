/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Zielu
 */
public class TimeSeriesModuleTest {
    
    public TimeSeriesModuleTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void serializedTS() throws JsonProcessingException, IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        DefaultPrettyPrinter  pp = (new DefaultPrettyPrinter())
                .withoutSpacesInObjectEntries()
                .withArrayIndenter(new DefaultPrettyPrinter.NopIndenter())
                .withObjectIndenter(new DefaultIndenter(" ", "\n"));
        mapper.setDefaultPrettyPrinter(pp);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.registerModule(new TimeSeriesModule());

        TimeSeries data = new TimeSeries();
        data.add(1,2);
        data.add(3,5);
        data.add(new Timepoint(4, 6, 0.1, 0.2));
        data.add(new Timepoint(5,7,0.2, Timepoint.STD_DEV));
        
        String json = mapper.writeValueAsString(data);
        //System.out.println(json); 
        
        assertNotNull(json);
        
        TimeSeries cpy = mapper.readValue(json, TimeSeries.class);
        assertEquals(data,cpy);
    }

    
}
