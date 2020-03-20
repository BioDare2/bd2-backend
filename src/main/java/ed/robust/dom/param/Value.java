package ed.robust.dom.param;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;

/**
 * Container for different values types that can be serialized and then converted to requested type
 * @author tzielins
 */
public class Value implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5870219224374106355L;
	private Serializable value;

    private Value() {
        
    };
    
    public Value(String val)
    {
	this.value = val;
    }


    public Value(int val)
    {
	this.value = val;
    }

    public Value(long val)
    {
	this.value = val;
    }

    public Value(boolean val)
    {
	this.value = val;
    }

    public Value(double val)
    {
	this.value = val;
    }
    
    public Value(Enum<?> val) {
        this.value = val;
    }

    @Override
    /**
     * Returns this value represented as String. Which is same as the toString method
     * of the underlying value object
     */
    public String toString()
    {
	if (value == null) return null;
        if (value.getClass().isEnum()) {
            return ((Enum)value).name();
        } else {
            return value.toString();
        }
    }

	/**
	 * Gives Integer representation of the value
	 * @return Integer value of null if conversion fails or no value
	 */
    public Integer toInteger() 
    {
		if (value == null) return null;
	
		if (value instanceof Integer) return ((Integer)value);
                if (value instanceof Number) return ((Number)value).intValue();
	
		try {
		    return Integer.parseInt(value.toString());
		} catch(NumberFormatException e) {}
		
		Double dbl = toDouble();
		
		if (dbl != null) return dbl.intValue();
		return null;
                
    }
    
    public <E extends Enum<E>> E toEnum(Class<E> elementType) {
        if (value == null) return null;
        if (elementType.isInstance(value)) return elementType.cast(value);
        String name = value.toString();
        E[] enums = elementType.getEnumConstants();
        if (enums == null) return null;
        for (E e: enums)
            if (e.name().equals(name)) return e;
        return null;
    }

	/**
	 * Gives Long representation of the value
	 * @return Long value of null if conversion fails or no value
	 */
    public Long toLong() 
    {
		if (value == null) return null;
	
		if (value instanceof Long) return ((Long)value);
                if (value instanceof Number) return ((Number)value).longValue();
	
		try {
		    return Long.parseLong(value.toString());
		} catch(NumberFormatException e) {
		    return null;
		}
    }

	/**
	 * Gives Double representation of the value
	 * @return Double value of null if conversion fails or no value
	 */
    public Double toDouble()
    {
		if (value == null) return null;
	
		if (value instanceof Double) return ((Double)value);
                if (value instanceof Number) return ((Number)value).doubleValue();
		try {
		    return Double.parseDouble(value.toString());
		} catch(NumberFormatException e) {
		    return null;
		}
    }

	/**
	 * Gives Boolean representation of the value
	 * @return Boolean value of null if conversion fails or no value
	 */
    public Boolean toBoolean() 
    {
	if (value == null) return null;

	if (value instanceof Boolean) return ((Boolean)value);

	return Boolean.parseBoolean(value.toString());

    }
    
    public Serializable getValue() {
    	return value;
    }

    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof Value)) return false;
        final Value v = (Value)obj;
        if (value != null) {
            if (value.getClass().isInstance(v.value))
                return value.equals(v.value);
            else return toString().equals(v.toString());
        }

        return (v.value == null);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
    
    
	/**
	 * Gives the value
	 * @return value of null if no value
	 */
    /*public Object toObject() 
    {
    	return value;

    }*/
    
    

}
