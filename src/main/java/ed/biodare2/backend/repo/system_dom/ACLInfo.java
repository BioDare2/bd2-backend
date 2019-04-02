/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author tzielins
 */
public class ACLInfo {
    
    @Version
    protected long recVersion;      
    
    @NotNull
    @NotBlank    
    public String creator;
    
    @NotNull
    @NotBlank    
    public String owner;
    
    @NotNull
    @NotBlank        
    public String superOwner;
    
    public boolean isPublic;
    
    public Set<String> allowedToRead = new HashSet<>();
    public Set<String> allowedToWrite = new HashSet<>();
}
