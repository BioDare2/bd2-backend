/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.openaccess;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author tzielins
 */
public class OpenAccessInfo implements Serializable {
    
    private static final long serialVersionUID = 3L;
    
    @NotNull
    @NotBlank    
    public String grantedByName;
    
    @NotNull
    @NotBlank    
    public String grantedByLogin;
        
    @NotNull
    @NotBlank    
    public LocalDateTime grantedOn;    
    
    @NotNull
    @NotBlank    
    public OpenAccessLicence licence;
    
    public String comment;
    
}
