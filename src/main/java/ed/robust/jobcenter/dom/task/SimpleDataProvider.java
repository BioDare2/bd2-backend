/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.task;

import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.ExpiredDataException;
import ed.robust.jobcenter.error.UnknownDataException;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author tzielins
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleDataProvider<T> extends DataProvider<T> {

    protected T data;
    
    public SimpleDataProvider() {
        
    }
    
    public SimpleDataProvider(T data) {
        this.data = data;
    }
    
    @Override
    public T getData() throws ConnectionException, UnknownDataException, ExpiredDataException {
        if (data == null) throw new UnknownDataException("Null data in the provider");
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    
    
}
