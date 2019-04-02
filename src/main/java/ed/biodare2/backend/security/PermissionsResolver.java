/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security;

import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.ui_dom.security.SecuritySummary;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class PermissionsResolver {
    
    
    //@Autowired
    public PermissionsResolver() {
    }


    
    
    
    public EntityACL createNewACL(BioDare2User user) {
        
        EntityACL acl = new EntityACL();
        
        acl.setCreator(user);
        acl.setOwner(user);
        acl.setSuperOwner(user.getSupervisor());
        
        user.getDefaultToWrite().forEach( acl::addCanWrite);
        user.getDefaultToWrite().forEach( acl::addCanRead);
        
        user.getDefaultToRead().forEach( acl::addCanRead);
        
        return acl;
        
    }
    
    public boolean canRead(EntityACL acl,BioDare2User user) {
        
        if (user == null) return false;
        if (acl.getOwner().equals(user)) return true;
        if (acl.getSuperOwner().equals(user)) return true;
        if (acl.isPublic()) return true;
        if (user.isAdmin()) return true;
        
        for (BioDare2Group group : user.getGroups()) {
            if (acl.getAllowedToRead().contains(group)) return true;
            if (acl.getAllowedToWrite().contains(group)) return true;
        }
        return false;
            
    }
    
    public boolean canWrite(EntityACL acl,BioDare2User user) {
        if (user == null) return false;
        if (acl.getOwner().equals(user)) return true;
        if (acl.getSuperOwner().equals(user)) return true;
        if (user.isAdmin()) return true;
        
        
        for (BioDare2Group group : user.getGroups()) {
            if (acl.getAllowedToWrite().contains(group)) return true;
        }
        
        return false;
        
    }
    
    public boolean isOwner(EntityACL acl, BioDare2User user) {
        if (user == null) return false;
        if (acl.getOwner().equals(user)) return true;
        if (acl.getSuperOwner().equals(user)) return true;
        return false;
    }    

    public SecuritySummary permissionsSummary(EntityACL acl, BioDare2User user) {
        SecuritySummary sec = new SecuritySummary();
        sec.isOwner = acl.getOwner().equals(user);
        sec.isSuperOwner = acl.getSuperOwner().equals(user);
        sec.canRead = canRead(acl, user);
        sec.canWrite = canWrite(acl, user);
        return sec;
    }

    public void makePublic(EntityACL acl) {
        acl.setPublic(true);
    }




    
    
}
