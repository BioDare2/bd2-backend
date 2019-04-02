/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author tzielins
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, 
              include = JsonTypeInfo.As.PROPERTY, 
              property = "_class_name")
@XmlSeeAlso({ExcelTSImportParameters.class})
public class TSImportParameters implements Serializable {
   
    static final long serialVersionUID = 11L;
    
}
