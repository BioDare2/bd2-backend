/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.tracking;

import ed.biodare2.backend.security.BioDare2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class AbstractTracker {
    
    final Logger operations = LoggerFactory.getLogger("operations");
    
    void track(TargetType target, ActionType action, BioDare2User user) {        
        track(target, action, 0, user);
    }    
    
    void track(TargetType target, ActionType action, long id, BioDare2User user) {
        operations.info("{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin());
    }    
    
    void track(TargetType target, ActionType action, String id, BioDare2User user) {
        operations.info("{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin());
    }    
    
    
    void track(TargetType target, ActionType action, long id, BioDare2User user,String subId) {
        operations.info("{}\t{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin(),subId);
    }     
    
    void track(TargetType target, ActionType action, long id, BioDare2User user,long subId) {
        operations.info("{}\t{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin(),subId);
    }     
    
    void track(TargetType target, ActionType action, String id, BioDare2User user,String subId) {
        operations.info("{}\t{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin(),subId);
    }     
    
    
    void track(TargetType target, ActionType action, long id, BioDare2User user,String cat,long subId) {
        operations.info("{}\t{}\t{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin(),subId,cat);
    }      
    
    void track(TargetType target, ActionType action, long id, BioDare2User user,String cat,long subId,long subId2) {
        operations.info("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}",target,action,id,user.getLogin(),user.getSupervisor().getLogin(),subId,cat,subId2);
    }      
    
}
