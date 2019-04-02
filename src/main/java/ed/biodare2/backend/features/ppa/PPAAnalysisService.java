/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.EnvironmentVariables;
import ed.robust.jobcenter.dom.job.JobHandle;
import ed.robust.jobcenter.dom.job.JobRequest;
import ed.robust.jobcenter.dom.job.RemoteRESTCallBack;
import ed.robust.jobcenter.dom.state.Status;
import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.SystemError;
import ed.robust.jobcenter.error.UnknownMethodException;
import ed.robust.jobcenter.error.ValidationException;
import ed.robust.jobcenter.ws.JobCenterClient;
import ed.robust.jobcenter.ws.JobCenterConnector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class PPAAnalysisService {

    final static String WSDL_STORAGE_DIR = "tmp_wsdl";
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final String localAdd;
    final String jobcentreWSDL;
    final Path wsdlTmpDir;    
    final String ppaUsername;
    final String ppaPassword; 
    
    @Autowired
    public PPAAnalysisService(EnvironmentVariables environment,
            final String ppaUsername,
            final String ppaPassword) {
        
        this.ppaUsername = ppaUsername;
        this.ppaPassword = ppaPassword;        
        this.localAdd = environment.backendURL.toString();
        this.jobcentreWSDL = environment.jobcentreURL.toString();
        Path storageDir = environment.storageDir;// Paths.get(storageDirPath);
        this.wsdlTmpDir = storageDir.resolve(WSDL_STORAGE_DIR);
        
        if (!Files.exists(wsdlTmpDir)) {
            try {
                Files.createDirectories(wsdlTmpDir);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Cannot create uploads storage dir: "+ex.getMessage(),ex);
            }
        }        
    }
 
    public String serviceStatus() throws ConnectionException {
        
        return getJobCenterConnector().getServiceStatus();
    }
    
    public JobHandle submitJob(JobRequest jobRequest) throws ConnectionException, PPAHandlingException  {

        jobRequest.setResultsHandler(getBioDare2CallBack());

        try {
            JobHandle jb = getJobCenterConnector().submitJob(jobRequest);
            log.info("Submitted job {} for exp {}",jb.getJobId(),jobRequest.getExternalId());
            
            return jb;
        } catch (UnknownMethodException| ValidationException | SystemError e) {
            log.error("Job submission error: "+e.getMessage(),e);
            throw new PPAHandlingException("Job was not accepted by the server: "+e.getMessage(),e);
        }
    }  
    
    public Status getJobStatus(JobHandle job) throws PPAHandlingException {
        
        try {
        Status st = getJobCenterConnector().getStatus(job);
        log.info("Checked status for job {} {}",job.getJobId(),st.getState());
        return st;
        } catch (ConnectionException e) {
            log.error("Job status error: "+e.getMessage(),e);
            throw new PPAHandlingException("Cannot get job status: "+e.getMessage(),e);
            
        }
    }
    
    
    protected JobCenterConnector getJobCenterConnector() throws ConnectionException {

            
            JobCenterConnector connector = new JobCenterClient(jobcentreWSDL, false, wsdlTmpDir.toFile());
            return connector;
    }    
    
    protected RemoteRESTCallBack getBioDare2CallBack() {

        //String localAdd = "http://localhost:9000"; 
        String url = localAdd+"/api/services/ppa/results";
        
        String oauthUrl = localAdd;
        String clientToken = "notuset";
        String user = ppaUsername;
        String pass = ppaPassword;
        
        
        RemoteRESTCallBack call = new RemoteRESTCallBack();
        call.url = url;
        call.outh_url = oauthUrl;
        call.client_auth_token = clientToken;
        call.user = user;
        call.password = pass;
        call.impl_class = "ed.biodare2.ppa.results.rest.ResultsReceiverBaisicAuthRestClient";
        
        return call;
    }    

}
