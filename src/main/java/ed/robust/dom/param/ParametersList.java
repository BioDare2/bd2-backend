/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.dom.param;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper over list of parameters, to have them nicely transported over wire for the WS. 
 * The class is used only, becouse depending of the JAX-WS implementation, the Paramters class despite its JAXB annotation was
 * not properly serialized to xml in JAX-WS client (The transient and xmlelemetn annotations were ignored). 
 * Should be used only over the wire,
 * normal using should be limited to Parameters and Values classes.
 * @author tzielins
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ParametersList implements Iterable<Parameter> {
    
    @XmlElement(name="p")
    protected List<Parameter> parameters;
    
    public ParametersList() {
        parameters = new ArrayList<Parameter>();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
    
    public void add(Parameter param) {
        getParameters().add(param);
    }
    
    /**
     * Changes this list of parameters into actual map of Name/Value. 
     * @return 
     */
    public Parameters toParameters() {
        Parameters params = new Parameters();
        for (Parameter p : parameters) params.put(p.name, p.value);        
        return params;
    }
    
    public static ParametersList parametersToList(Parameters params) {
        ParametersList list = new ParametersList();
        for (java.util.Map.Entry<String, Value> entry : params.entrySet()) {
            list.add(new Parameter(entry.getKey(),entry.getValue()));
        }
        return list;
    }

    @Override
    public Iterator<Parameter> iterator() {
        return getParameters().iterator();
    }
    
    
    
}
