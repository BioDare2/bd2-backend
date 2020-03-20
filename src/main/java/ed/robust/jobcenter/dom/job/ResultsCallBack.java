/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.SystemError;
import ed.robust.jobcenter.error.UnknownDataException;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author tzielins
 */
@XmlSeeAlso({RemoteWSCallBack.class,RemoteRESTCallBack.class, DebugResultsCallBack.class})
public abstract class ResultsCallBack<T extends Serializable> implements Serializable {
    
    public abstract void handleResults(long jobId,String externalId,T results) throws SystemError, ConnectionException, UnknownDataException;
    
}
