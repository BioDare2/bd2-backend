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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author tzielins
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class RemoteRESTCallBack<T extends Serializable> extends ResultsCallBack<JobResult<T>> {

    static final long serialVersionUID = 7683152751681936169L;
    
    @XmlAttribute
    public String url;
    @XmlAttribute
    public String user;
    @XmlAttribute
    public String password; 
    
    @XmlAttribute
    public String client_auth_token;
    
    @XmlAttribute
    public String outh_url;    
    
    @XmlAttribute
    public String impl_class;
    
    //public final static String IMPL_CLASS = "ed.biodare2.ppa.results.rest.ResultsReceiverRestClient";
    public final static String IMPL_CLASS = "ed.biodare2.ppa.results.rest.ResultsReceiverBaisicAuthRestClient";
    transient protected RESTConnector connector;
    

    @Override
    public void handleResults(long jobId, String externalId, JobResult<T> results) throws SystemError, ConnectionException, UnknownDataException {
        
        getConnector().handleResults(jobId, externalId, results, this);
    }

    protected RESTConnector getConnector() throws SystemError {
        
        if (connector == null) {
        
            try {
                if (impl_class == null || impl_class.isEmpty())
                    impl_class = IMPL_CLASS;
                
                Class conClass = Class.forName(impl_class);        
                connector = (RESTConnector)conClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new SystemError("Cannot find RESTClient implementations ("+IMPL_CLASS+"); "+e.getMessage(),e);
            } catch (InstantiationException|IllegalAccessException e) {
                throw new SystemError("Cannot initialize RESTClient implementations ("+IMPL_CLASS+"); "+e.getMessage(),e);
            } 
        };
        return connector;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.url);
        hash = 43 * hash + Objects.hashCode(this.outh_url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RemoteRESTCallBack<?> other = (RemoteRESTCallBack<?>) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.client_auth_token, other.client_auth_token)) {
            return false;
        }
        if (!Objects.equals(this.outh_url, other.outh_url)) {
            return false;
        }
        return true;
    }
    
    
    
}
