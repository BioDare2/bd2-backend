package ed.robust.dom.param;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Utility class that represents sets of parameter's values accessed by their names and providing conversion values.
 * The class was intended to be serializable to the xml over WS but the serialization doesn't seem to work over JAX_WS clients
 * it means that the Transient and converions to paramters is ignored and raises error. However it works locally with JAXB
 * @author tzielins
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
@XmlSeeAlso({Parameter.class})
@XmlJavaTypeAdapter(ParametersAdapter.class)
public class Parameters implements /*Map<String,Value>,*/ Serializable, Cloneable {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6085887487144158869L;
        
	//@XmlTransient
        @XmlJavaTypeAdapter(ValueMapAdapter.class)
        @XmlElement(name="params")        
	protected Map<String,Value> map;
	
        /*@XmlElement(name="p")
        transient List<Parameter> params;
        */
        
        public Parameters() {
            map = new HashMap<>();
        }
        
    //@XmlElement(name="p")
    /*public List<Parameter> getParameters()
    {
    	List<Parameter> list = new ArrayList<Parameter>();
    	for (Entry<String,Value> entry: map.entrySet()) {
    		list.add(new Parameter(entry.getKey(),entry.getValue()));
    	}
    	return list;
    }*/
        
    /*boolean beforeMarshal(Marshaller mar) {
        //System.out.println("Before marsharller called on "+this.getClass().getSimpleName());
        params = buildParamList(map);
        return true;
    }

    void afterUnmarshal(Unmarshaller unm, Object parent) {
        //System.out.println("After unmarsharller called"+this.getClass().getSimpleName());
        map = buildParamMap(params);
    }
        

    protected void setParameters(List<Parameter> parameters) {
        params = parameters;
        afterUnmarshal(null, null);
    }

    protected Map<String, Value> buildParamMap(List<Parameter> params)
    {
    	Map<String, Value> m = new HashMap<String, Value>();
        if (params != null)
            for (Parameter p : params)
    		m.put(p.getName(), p.getValue());
        return m;
    }
    
    protected List<Parameter> buildParamList(Map<String, Value> map)
    {
    	List<Parameter> list = new ArrayList<Parameter>();
    	for (Entry<String,Value> entry: map.entrySet()) {
    		list.add(new Parameter(entry.getKey(),entry.getValue()));
    	}
    	return list;
    }    
    
    
    public ParametersList toParametersList() {
        ParametersList p = new ParametersList();
        p.setParameters(buildParamList(map));
        return p;
    }*/
	

	public Value put(String key, String value) {
		return put(key, new Value(value));
	}
        
	public Value put(String key, int value) {
		return put(key, new Value(value));
	}
        
	public Value put(String key, long value) {
		return put(key, new Value(value));
	}
        
	public Value put(String key, boolean value) {
		return put(key, new Value(value));
	}
        
	public Value put(String key, double value) {
		return put(key, new Value(value));
	}
        
        public Value put(String key,Enum<?> value) {
            return put(key,new Value(value));
        }
        
        
//	@Override
	public Value put(String key, Value value) {
            return map.put(key, value);
	}

        
	/**
	 * Retrieves actual value under the key converted to the String type
	 * @param key 
	 * @return String representation of the value or null if no value under the key
	 */
	public String getString(String key) {
		Value val = get(key);
		if (val == null) return null;
		return val.toString();
	}
	
	/**
	 * Retrieves the String value under the key, returning the defaultValue if there is no such key.
	 * @param key 
	 * @param defaultValue value which will be returned if the value is missing
	 * @return String representation of the value or default value if no such key (other types values types are always converted into Strings)
	 */
	public String getString(String key,String defaultValue) {
		String val = getString(key);
		if (val == null) return defaultValue;
		return val;
	}
	
	
	/**
	 * Retrieves actual value under the key converted to the Integer type
	 * @param key
	 * @return Integer representation of the value or null if conversion fails or no value under the key
	 */
	public Integer getInteger(String key) {
		Value val = get(key);
		if (val == null) return null;
		return val.toInteger();
	}
	
	/**
	 * Retrieves the int value under the key, returning the defaultValue in case conversion fails or no such key.
	 * @param key 
	 * @param defaultValue value which will be returned if the entry value cannot be converted into int (wrong type or null)
	 * @return the integer values under given key or the defaultValue if the value under key is not an int (wrong type or null)
	 */
	public int getInt(String key,int defaultValue) {
		Integer val = getInteger(key);
		if (val == null) return defaultValue;
		return val.intValue();
	}
	
	
	
	/**
	 * Retrieves actual value under the key converted to the Long type
	 * @param key
	 * @return Long representation of the value or null if conversion fails or no value under the key
	 */
	public Long getLong(String key) {
		Value val = get(key);
		if (val == null) return null;
		return val.toLong();
	}
        
	public long getLong(String key,long defaultValue) {
                Long val = getLong(key);
                if (val == null) return defaultValue;
                return val.longValue();
	}
        
	
	/**
	 * Retrieves the double value under the key, returning the defaultValue in case conversion fails or no such key.
	 * @param key 
	 * @param defaultValue value which will be returned if the entry value cannot be converted into double (wrong type or null)
	 * @return the double values under given key or the defaultValue if the value under key is not a double (wrong type or null)
	 */
	public double getDouble(String key,double defaultValue) {
		Double val = getDouble(key);
		if (val == null) return defaultValue;
		return val.doubleValue();
	}
	
	/**
	 * Retrieves actual value under the key converted to the Double type
	 * @param key
	 * @return Double representation of the value or null if conversion fails or no value under the key
	 */
	public Double getDouble(String key) {
		Value val = get(key);
		if (val == null) return null;
		return val.toDouble();
	}
	
	
	/**
	 * Retrieves actual value under the key converted to the Boolean type
	 * @param key
	 * @return Boolean representation of the value or null if conversion fails or no value under the key
	 */
	public Boolean getBoolean(String key) {
		Value val = get(key);
		if (val == null) return null;
		return val.toBoolean();
	}
	
	/**
	 * Retrieves the boolean value under the key, returning the defaultValue in case conversion fails or no such key.
	 * @param key 
	 * @param defaultValue value which will be returned if the entry value cannot be converted into boolean (wrong type or null)
	 * @return the boolean values under given key or the defaultValue if the value under key is not a boolean (wrong type or null)
	 */
	public boolean getBool(String key,boolean defaultValue) {
		Boolean val = getBoolean(key);
		if (val == null) return defaultValue;
		return val.booleanValue();
	}
        
        public <E extends Enum<E>> E getEnum(String key,Class<E> elementType) {
 		Value val = get(key);
		if (val == null) return null;
		return val.toEnum(elementType);
        }       
        
        public <E extends Enum<E>> E getEnum(String key,E defaultValue) {
                if (defaultValue == null) throw new IllegalArgumentException("Default value cannot be null");
 		E val = getEnum(key,defaultValue.getDeclaringClass());
		if (val == null) return defaultValue;
		return val;
        }       
        
	
	/**
	 * Retrieves actual value under the key
	 * @param key
	 * @return the value or null if no value under the key
	 */
	/*public Object getObject(Object key) {
		Value val = get(key);
		if (val == null) return null;
		return val.toObject();
	}*/
	
	
//	@Override
	public void clear() {
		map.clear();		
	}

//	@Override
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

//	@Override
	public boolean containsValue(Value value) {
		return map.containsValue(value);
	}

//	@Override
	public Set<java.util.Map.Entry<String, Value>> entrySet() {
		return map.entrySet();
	}

//	@Override
	public Value get(String key) {
		return map.get(key);
	}

//	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

//	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

//	@Override
	public void putAll(Map<? extends String, ? extends Value> vals) {
		map.putAll(vals);
	}

//	@Override
	public Value remove(String key) {
		return map.remove(key);
	}

//	@Override
	public int size() {
		return map.size();
	}

//	@Override
	public Collection<Value> values() {
		return map.values();
	}

	//@SuppressWarnings("unchecked")
	@Override
	public Parameters clone() {
		
		Parameters np = null;
		try {
			np = (Parameters)super.clone();
                        np.map = new HashMap<>();
                        for (Map.Entry<String,Value> entry : entrySet()) np.put(entry.getKey(), entry.getValue());
		} catch (CloneNotSupportedException e) {
                    throw new RuntimeException("Parameters could not be cloned: "+e.getMessage(),e);
                }
		return np;
	}
	
	/**
	 * Sets the parameters values using content of the provided map but only if there is no corresponding value
	 * in the current object. So the values are set only for the missing keys in the current object.
	 * This method is intended to be used to merge specific parameters with the 'global' default ooptiosn.
	 * By calling this method the default values may be provided, but all the original entries will be presserved,
	 * so the default values do not overwrite the specific setting.
	 * @param defaultValues Map container with the default values, for all keys which are missing
	 * in the current object, the values from defaultValues will be used to set such values. 
	 */
	public void merge(Map<String,Value> defaultValues) {
		
		for (String key : defaultValues.keySet()) {
			if (containsKey(key)) continue;
			put(key,defaultValues.get(key));
		}
	}
        
        public void merge(Parameters defaultValues) {
            merge(defaultValues.map);
        }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Entry<String,Value> entry: map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue().toString());
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.map != null ? this.map.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Parameters)) {
            return false;
        }
        final Parameters other = (Parameters) obj;        
        if (this.map != other.map && (this.map == null || !this.map.equals(other.map))) {
            return false;
        }
        return true;
    }

    public HashMap<String, String> toStringMap() {
        HashMap<String, String> res = new HashMap<>();
        for (String key : map.keySet()) {
            String val = getString(key);
            if (val != null) res.put(key, val);                    
        }
        return res;
        
    }
        
        
}
