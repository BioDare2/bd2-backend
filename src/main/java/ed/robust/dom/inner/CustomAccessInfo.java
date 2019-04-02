package ed.robust.dom.inner;

import java.text.DateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public abstract class CustomAccessInfo {

	protected DateFormat dateFormat;
	
	public Date getJavaDate()
	{
		return getDate().toGregorianCalendar().getTime();
	}
	
	public abstract XMLGregorianCalendar getDate();
	
	public String getStringDate()
	{
		//System.out.println("Get date called: "+getJavaDate());
		return getDateFormat().format(getJavaDate()); 
	}
	
	protected DateFormat getDateFormat()
	{
		if (dateFormat == null) dateFormat = DateFormat.getDateInstance();
		return dateFormat;
	}
}
