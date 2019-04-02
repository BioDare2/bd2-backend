package ed.robust.ppa.results.ws;

import ed.robust.jobcenter.dom.job.JobResult;
import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.SystemError;
import ed.robust.jobcenter.error.UnknownDataException;
import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public interface ResultsReceiverConnector<T extends Serializable> {
    
    public String getServiceStatus() throws ConnectionException;
    
    public void handleResults(long jobId,String externalId,JobResult<T> results) throws SystemError, ConnectionException, UnknownDataException;
    
}
