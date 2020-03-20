/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.SystemError;
import ed.robust.jobcenter.error.UnknownDataException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class DebugResultsCallBack<T extends Serializable> extends ResultsCallBack<JobResult<T>> {

    static final Logger logger = LoggerFactory.getLogger(DebugResultsCallBack.class);
    
    @Override
    public void handleResults(long jobId, String externalId, JobResult<T> results) throws SystemError, ConnectionException, UnknownDataException {

        StringBuilder sb = new StringBuilder();
        sb.append("\nDUMMY results handling for job: ").append(jobId).append("(").append(externalId).append(")").append("\n");
        if (results == null) sb.append("Null job results").append("\n");
        else {
            sb.append("Job state: ").append(results.getJobState()).append(" ");
            if (results.getTaskResults() == null) sb.append("Null task results\n");
            else {
                sb.append("sub results: ").append(results.getTaskResults().size()).append("\n");
                for (TaskResult<T> task : results) {
                    sb.append("Task: ").append(task.getTaskId()).append(" ").append(task.getState()).append(", MSG:").append(task.getMessage()).append("; ");
                    if (task.getResult() == null) sb.append("null result");
                    else {
                        sb.append(task.getResult().getClass().getSimpleName());
                    }
                    sb.append("\n");
                    
                }
            }
        }
        logger.info(sb.toString());
    }
    
}
