/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.tracking;

import ed.biodare2.backend.security.BioDare2User;
import org.springframework.stereotype.Service;
import static ed.biodare2.backend.web.tracking.TargetType.*;
import static ed.biodare2.backend.web.tracking.ActionType.*;
/**
 *
 * @author tzielins
 */
@Service
public class AccountTracker extends AbstractTracker {

    public void userView(String login, BioDare2User currentUser) {
        track(ACCOUNT,VIEW,login,currentUser);
    }

    public void userCheck(String login, BioDare2User currentUser) {
        track(ACCOUNT,CHECK,login,currentUser);
    }
    
    public void userEmailCheck(String email, BioDare2User currentUser) {
        track(ACCOUNT,CHECK,email,currentUser);
    }    

    public void userNew(BioDare2User account, BioDare2User user) {
        track(ACCOUNT,NEW,account.getLogin(),user);
    }

    public void userUpdate(BioDare2User account, BioDare2User user) {
        track(ACCOUNT,UPDATE,account.getLogin(),user);
    }

    public void userActivated(BioDare2User account, BioDare2User user) {
        track(ACCOUNT,ACTIVATED,account.getLogin(),user);
    }

    public void userPasswordReset(BioDare2User account, BioDare2User user) {
        track(ACCOUNT,RESET,account.getLogin(),user);
    }


    
}
