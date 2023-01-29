/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.xml;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import ed.biodare2.backend.web.rest.ServerSideException;

//import ed.robust.dom.tsprocessing.PPAResult;
import java.nio.file.Path;
//import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author tzielins
 */
public class XMLUtil {

    static LoadingCache<Class, JAXBContext> cache;
    
    static {
         cache = Caffeine.newBuilder()
            .maximumSize(50)
            .build(key -> makeJaxbContext(key));
         
        
        /*try {
            //cache.put(JobResult.class, JAXBContext.newInstance(JobResult.class,PPAResult.class));         
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }*/
    }

    static JAXBContext makeJaxbContext(Class key) {
        try {
            return JAXBContext.newInstance(key);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void saveToFile(Object elm, Path file) {
        
        //JAXB.marshal(elm, file.toFile());
        
        try {
            JAXBContext ctx = cache.get(elm.getClass());
            Marshaller mar = ctx.createMarshaller();
            mar.marshal(elm, file.toFile());
                
        } catch (JAXBException e) {
           throw new ServerSideException("Cannot save to xml: "+e.getMessage(),e);
        }
    }
    
    //public void savePPAToFile(JobResult elm, Path file) {

    //    saveToFile(elm,file);
        /*
        try {
        JAXBContext ctx = JAXBContext.newInstance(elm.getClass(),PPAResult.class);
        Marshaller mar = ctx.createMarshaller();
        mar.marshal(elm, file.toFile());
                
        } catch (JAXBException e) {
           //e.printStackTrace();
           throw new RuntimeException(e);
        }*/
    //}    

    public <T> T readFromFile(Path file, Class<T> aClass) {
        //return JAXB.unmarshal(file.toFile(), aClass);
        try {
            JAXBContext ctx = cache.get(aClass);
            JAXBElement<T> item = ctx.createUnmarshaller().unmarshal(new StreamSource(file.toFile()), aClass);
            return item.getValue();                
        } catch (JAXBException e) {
           throw new ServerSideException("Cannot read from xml: "+e.getMessage(),e);
        }
        
    }
    
    
    
    
}
