/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security;

import ed.biodare2.backend.features.rdmsocial.RDMUserAspect;
import ed.biodare2.backend.features.subscriptions.AccountSubscription;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author tzielins
 */
public interface BioDare2User extends UserDetails {

    String getEmail();
    
    String getInitialEmail();

    String getFirstName();

    Long getId();

    String getLastName();

    String getLogin();

    String getName();
    
    String getORCID();
    
    String getInstitution();
    
    boolean isPI();
    
    boolean isAdmin();
    

    boolean isAnonymous();

    boolean isBackendOnly();

    boolean isLocked();

    boolean isSystem();
    
    boolean isReadOnly();
    
    boolean hasDirtySession();
    void setDirtySession(boolean dirty);

    @Override
    public List<GrantedAuthority> getAuthorities();
    
    BioDare2User getSupervisor();
    
    public Set<BioDare2Group> getGroups();
    
    public Set<BioDare2Group> getSystemGroups();
    
    
    //public  List<BioDare2Group> getUserGroups();

    //public List<BioDare2Group> getSystemGroups();

    //public List<BioDare2Group> getSpecialGroups();

    public void addGroup(BioDare2Group group);

    /**
     * Gives list of groups for which read permission should be granted as the default option
     * for any resources produced by this user
     * @return list of groups
     */
    public Set<BioDare2Group> getDefaultToRead();

    public Set<BioDare2Group> getDefaultToWrite();

    public void addDefaultToRead(BioDare2Group group);

    public void addDefaultToWrite(BioDare2Group group);    
    
    public AccountSubscription getSubscription();
    
    public RDMUserAspect getRdmAspect();
}
