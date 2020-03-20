/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.dom.param;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author tzielins
 */
public class ParametersAdapter extends XmlAdapter<ParametersList,Parameters>{

    @Override
    public Parameters unmarshal(ParametersList list) throws Exception {
        if (list == null) return null;
        return list.toParameters();
    }

    @Override
    public ParametersList marshal(Parameters map) throws Exception {
        if (map == null) return null;
        return ParametersList.parametersToList(map);
    }
    
}
