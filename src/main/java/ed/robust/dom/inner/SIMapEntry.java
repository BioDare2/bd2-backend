/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ed.robust.dom.inner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author tzielins
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "k",
    "v"
})
public class SIMapEntry {

    @XmlAttribute(name="k")
    String k;
    @XmlAttribute(name="v")
    String v;

    public SIMapEntry() {

    }

    public SIMapEntry(String k, String v)
    {
	this.k = k;
	this.v = v;
    }

    public static List<SIMapEntry> toList(Map<String,String> map)
    {

	List<SIMapEntry> list = new ArrayList<SIMapEntry>();
	if (map == null) return list;
	for (String k : map.keySet())
	{
	    list.add(new SIMapEntry(k,map.get(k)));
	}
	return list;
    }

    public static Map<String,String> toMap(List<SIMapEntry> list)
    {
	Map<String,String> map = new HashMap<String, String>();
	if (list == null) return map;
	for (SIMapEntry en : list)
	    map.put(en.getKey(), en.getValue());
	return map;
    }


    public String getKey() {
	return k;
    }

    public void setKey(String key) {
	this.k = key;
    }

    public String getValue() {
	return v;
    }

    public void setValue(String value) {
	this.v = value;
    }


}
