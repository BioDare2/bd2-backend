/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.SystemError;
import ed.robust.jobcenter.error.UnknownDataException;
//import ed.robust.ppa.results.ws.ResultsReceiverClient;
import ed.robust.ppa.results.ws.ResultsReceiverConnector;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author tzielins
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class RemoteWSCallBack<T extends Serializable> extends ResultsCallBack<JobResult<T>> {

    static final long serialVersionUID = 7683152751681936169L;
    
    @XmlAttribute
    protected String wsdl;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String namespace;

    transient protected final boolean threadSafe = false;
    
    transient protected ResultsReceiverConnector<T> connector;
    transient private static final ConcurrentHashMap<String,ResultsReceiverConnector> connectors = new ConcurrentHashMap<String,ResultsReceiverConnector>();
    
    public RemoteWSCallBack() {
        
    }
    
    public RemoteWSCallBack(String wsdl, String name, String namespace) {
        this.wsdl = wsdl;
        this.name = name;
        this.namespace = namespace;
    }
    
    
    
    @Override
    public void handleResults(long jobId,String externalId,JobResult<T> results) throws SystemError, ConnectionException, UnknownDataException {
        
        ResultsReceiverConnector<T> receiver = getReceiver();
        
        receiver.handleResults(jobId, externalId, results);
    }
    
    public String getServiceStatus() throws ConnectionException {
        ResultsReceiverConnector<T> receiver = getReceiver();
        return receiver.getServiceStatus();
    }

    protected ResultsReceiverConnector<T> getReceiver() throws ConnectionException {
        
        if (connector == null) {
            connector = getConnector(wsdl,namespace,name);
            if (connector == null) {
                connector = makeNewConnector();
                registerConnector(connector,wsdl,namespace,name);
            }
        }
        return connector;
    }
    
    protected ResultsReceiverConnector<T> makeNewConnector() throws ConnectionException {
        
        //return new ResultsReceiverClient<>(wsdl, namespace, name, threadSafe);
        throw new UnsupportedOperationException("after dom simplification");
    }
    

    protected static ResultsReceiverConnector getConnector(String wsdl, String namespace, String name) {
        
        String key = makeKey(wsdl,namespace,name);
        ResultsReceiverConnector connector = connectors.get(key);
        return connector;
        
    }

    protected static synchronized void registerConnector(ResultsReceiverConnector connector,String wsdl, String namespace, String name) {
         String key = makeKey(wsdl, namespace, name);
         connectors.put(key, connector);
    }
    
    protected static String makeKey(String wsdl, String namespace, String name) {
        return ""+wsdl+":"+namespace+":"+name;
    }

    public String getWsdl() {
        return wsdl;
    }

    
    
    
    
}
