/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.SystemError;
import ed.robust.jobcenter.error.UnknownDataException;
import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public interface RESTConnector {
    
    public <T extends Serializable> void handleResults(long jobId, String externalId, JobResult<T> results,RemoteRESTCallBack<T> callback)throws SystemError, ConnectionException, UnknownDataException;
}
