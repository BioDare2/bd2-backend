/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import java.util.Map;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/user")
public class AuthController extends BioDare2Rest {
    
    //final UserAccountRep accounts;
    
    
    /*@Autowired
    public AuthController(UserAccountRep accounts) {        
        this.accounts = accounts;
    }*/
    
    @RequestMapping(method = RequestMethod.GET)
    public Map<String,Object> user(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("Current User {}",(currentUser != null ? currentUser.getLogin() : "Null user"));
        
        /*if (!currentUser.isAnonymous()) {
            currentUser = accounts.findOne(currentUser.getId());
        }*/
        return AccountController.account2UserMap(currentUser);
    } 
    

}
