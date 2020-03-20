/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.task;

import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.ExpiredDataException;
import ed.robust.jobcenter.error.UnknownDataException;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author tzielins
 */
@XmlSeeAlso({SimpleDataProvider.class})
public abstract class DataProvider<T> implements Serializable {
    
    
    public abstract T getData() throws ConnectionException, UnknownDataException, ExpiredDataException;
    
}
