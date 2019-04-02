/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class FileImportRequest implements Serializable {
   
    static final long serialVersionUID = 11L;
    
    public String fileId;
    public ImportFormat importFormat;
    public TSImportParameters importParameters;
}
