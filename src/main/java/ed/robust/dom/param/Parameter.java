package ed.robust.dom.param;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;


/**
 * Classed used to serialize paramters map into list of values. Should be used only 'over the wire', for normal using
 * the Paramereters and Values are better choice as their offer data conversions.
 * @author tzielins
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class Parameter implements Serializable {

	/*protected static final int OBJECT = 0;
	protected static final int INT = 1;
	protected static final int LONG = 2;
	protected static final int DOUBLE = 3;
	protected static final int BOOLEAN = 4;
	protected static final int STRING = 5;
	*/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6017375345407641012L;
	@XmlAttribute(name="n", required = true)
	String name;
	@XmlAttribute(name="v", required = true)
	String value;
	
	//@XmlAttribute(name="t", required = true)
	//int type;
	
	public Parameter() {
		super();		
	}
	
	public Parameter(String name, Value value) {
		this();
		this.name = name;
		if (value != null) this.value = value.toString();
		//this.type = getTypeCode(value);		
	}
	
	/*protected static final int getTypeCode(Object obj) {
		if (obj == null) return OBJECT;
		if (obj instanceof Integer) return INT;
		if (obj instanceof Long) return LONG;
		if (obj instanceof Double) return DOUBLE;
		if (obj instanceof Boolean) return BOOLEAN;
		if (obj instanceof String) return STRING;
		return OBJECT;		
	}*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Value getValue() {
		return new Value(value);
		/*if (value == null) return null;
		
		if (type == OBJECT) return new Value(value);
		if (type == INT) return new Value(Integer.parseInt(value));
		if (type == LONG) return new Value(Long.parseLong(value));
		if (type == )
		return value;
		*/
	}

        @Override
        public String toString() {
            return "["+name+"="+value+"]";
        }
	
}
