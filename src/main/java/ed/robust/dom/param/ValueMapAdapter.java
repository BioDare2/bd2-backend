/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.dom.param;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author tzielins
 */
public class ValueMapAdapter extends XmlAdapter<ParametersList,Map<String,Value>>{

    @Override
    public Map<String, Value> unmarshal(ParametersList list) throws Exception {
        if (list == null) return null;
        Map<String,Value> map = new HashMap<>();
        for (Parameter p : list) {
            map.put(p.name, new Value(p.value));
        }
        return map;
    }

    @Override
    public ParametersList marshal(Map<String, Value> map) throws Exception {
        if (map == null) return null;
        ParametersList list = new ParametersList();
        for (Map.Entry<String,Value> entry : map.entrySet()) list.add(new Parameter(entry.getKey(),entry.getValue()));
        
        return list;
    }
    
}
