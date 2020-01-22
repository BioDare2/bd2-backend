/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.services.recaptcha.ReCaptchaService;
import ed.biodare2.backend.web.tracking.AccountTracker;
import ed.biodare2.backend.web.tracking.SecurityTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Zielu
 */
@RestController
@RequestMapping("api/account")
public class AccountController extends BioDare2Rest {
    
    final UsersHandler usersHandler;
    final AccountTracker tracker;
    final SecurityTracker securityTracker;
    
    UserAccountRep accounts;
    ReCaptchaService captcha;
    
    @Autowired
    public AccountController(UserAccountRep accounts,UsersHandler usersHandler,ReCaptchaService captcha,AccountTracker tracker,SecurityTracker securityTracker) {
        this.accounts = accounts;
        this.usersHandler = usersHandler;
        this.captcha = captcha;
        this.tracker = tracker;
        this.securityTracker = securityTracker;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Map<String,Object> currentAccount(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("get current account: {}",currentUser);
        //tracker.userView(currentUser.getLogin(),currentUser);
        //return account2UserMap(currentUser);
        return account(currentUser.getLogin(),currentUser);
    }   
    
    @RequestMapping(value = "{login}",method = RequestMethod.GET)
    public Map<String,Object> account(@PathVariable String login,@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("get account: {}; {}",login,currentUser);
        
        try {
        Map<String,Object> resp = accounts.findByLogin(login.toLowerCase())
                        .map(AccountController::account2UserMap)
                        .orElseThrow(()-> new NotFoundException("Account "+login+" not found"));
        
        tracker.userView(login,currentUser);
        return resp;
        } catch(WebMappedException e) {
            log.error("Cannot get account {} {}",login,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get account {} {}",login,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        

    }     
    
    //it has to be post method so the email will not be truncated from get pathway on the last dot.
    @RequestMapping(value = "available-login",method = RequestMethod.POST)
    public boolean available(@NotNull @RequestBody String login,@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("check login: {}; {}",login,currentUser);
        
        login = login.toLowerCase();
        if ("available-login".equals(login)) return false;
        if ("academic-email".equals(login)) return false;
        if ("suitable-email".equals(login)) return false;
        
        try {
            
            boolean resp = !accounts.findByLogin(login).isPresent();
            tracker.userCheck(login,currentUser);
            return resp;
        
        } catch(WebMappedException e) {
            log.error("Cannot check login {} {}",login,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot check login {} {}",login,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    }
    


    //it has to be post method so the email will not be truncated from get pathway on the last dot.
    @RequestMapping(value = "suitable-email",method = RequestMethod.POST)
    public UsersHandler.EmailSuitability isSuitableEmail(@NotNull @RequestBody String email,@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("isSuitableEmail email: {}; {}",email,currentUser);
        
        try {
        email = email.toLowerCase();
        UsersHandler.EmailSuitability suit = usersHandler.isSuitableEmail(email);
        tracker.userEmailCheck(email,currentUser);
        return suit;
        
        } catch(WebMappedException e) {
            log.error("Cannot check email {} {}",email,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot check email {} {}",email,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    }     
    
    protected void verifyCaptcha(String email,String captchaResponse) {
        if (captchaResponse.isEmpty()) {
            if (email.endsWith(".cn") || email.endsWith(".tw")) {
                log.warn("Skipping captcha for: {}",email);
            } else  {
                throw new HandlingException("Missing captcha");
            }
        } else {
            //log.warn("Captcha disabled");
            if (!captcha.verify(captchaResponse))
                throw new HandlingException("Wrong captcha");
        }        
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public Map<String,Object> register(@NotNull @RequestBody Map<String,String> userDetails,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("register account: {} {}; {}",userDetails.get("login"),userDetails.get("email"),user);
        
        if (!user.isAnonymous())
            throw new HandlingException("You are already using full account");
        
        String email = userDetails.getOrDefault("email", "");
        String captchaResponse = userDetails.getOrDefault("g_recaptcha_response","");
        
        verifyCaptcha(email,captchaResponse);
        
        try {
            BioDare2User account = usersHandler.register(userDetails);
            tracker.userNew(account,user);
            log.info("Registered account: "+account.getLogin());
            
            usersHandler.sendActivationEmail(account);
            log.info("Sent activation email");
            
            return account2UserMap(account);
        } catch(WebMappedException e) {
            log.error("Cannot register user: {}",email,e.getMessage(),e);
            throw e;
        } catch (UsersHandler.AccountHandlingException e) {
            log.error("Cannot register user: {}",e.getMessage(),e);
            throw new HandlingException(e.getMessage(),e);
        } catch (Exception e) {
            log.error("Cannot register user: {}",e.getMessage(),e);
            throw new HandlingException(e.getMessage(),e);
            
        }
    } 
    
    @RequestMapping(value="activate",method = RequestMethod.POST)
    public Map<String,Object> activate(@NotNull @RequestBody String token,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("activate account: {}; {}",token,user);
        
        
        
        
        try {
            BioDare2User activated = usersHandler.activateAccount(token);
            tracker.userActivated(activated,user);
            log.info("Activated account: "+activated.getLogin());
            
            return account2UserMap(activated);
        } catch (UsersHandler.AccountHandlingException e) {
            throw new HandlingException(e.getMessage(),e);
        } catch(WebMappedException e) {
            log.error("Cannot acctivate account {}",token,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot acctivate account {}",token,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    } 
    

    @RequestMapping(method = RequestMethod.POST)
    public Map<String,Object> update(@NotNull @RequestBody Map<String,String> userDetails,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("update account: {}; {}",userDetails.get("login"),user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to edit account");
        
        if (!user.getLogin().equals(userDetails.get("login")))
            throw new InsufficientRightsException("You can only edit your own account");
        
        try {
            BioDare2User account = usersHandler.update(userDetails,user);
            user.setDirtySession(true);
            tracker.userUpdate(account,user);
            log.info("Updated account: "+account.getLogin());
            return account2UserMap(account);
        } catch (UsersHandler.AccountHandlingException e) {
            log.error("Cannot update account {} {}",userDetails.get("login"),e.getMessage(),e);
            throw new HandlingException(e.getMessage(),e);
        } catch(WebMappedException e) {
            log.error("Cannot update account {} {}",userDetails.get("login"),e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot update account {} {}",userDetails.get("login"),e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    } 

    
    @RequestMapping(value="password", method = RequestMethod.POST)
    public Map<String,Object> updatePassword(@NotNull @RequestBody Map<String,String> userDetails,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("update password: {}; {}",userDetails.get("login"),user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to edit account");
        
        if (!user.getLogin().equals(userDetails.get("login")))
            throw new InsufficientRightsException("You can only edit your own account");
        
        try {
            BioDare2User account = usersHandler.updatePassword(userDetails,user);
            user.setDirtySession(true);
            tracker.userUpdate(account,user);
            log.info("Updated password: "+account.getLogin());
            return account2UserMap(account);
        } catch (UsersHandler.AccountHandlingException e) {
            throw new HandlingException(e.getMessage(),e);
        } catch(WebMappedException e) {
            log.error("Cannot update password {} {}",userDetails.get("login"),e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot update password {} {}",userDetails.get("login"),e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    } 
    
    @RequestMapping(value="remind",method = RequestMethod.POST)
    public Map<String,Object> remind(@NotNull @RequestBody Map<String,String> details,@NotNull @AuthenticationPrincipal BioDare2User user) {
        
        String identifier = details.getOrDefault("identifier","");
        log.debug("remind account: {}; {}",identifier,user);
        
        
        Optional<BioDare2User> account = usersHandler.identify(identifier);
        
        if (!account.isPresent()) {
            try {
                //delay to stop from spamming
                Thread.sleep(1000);
            } catch (InterruptedException e){};
            throw new HandlingException("Unkown account for: "+identifier);            
        }
        
        String captchaResponse = details.getOrDefault("g_recaptcha_response","");
        String email = account.get().getInitialEmail();
        verifyCaptcha(email,captchaResponse);        
        
        
        try {
            
            BioDare2User reminded = usersHandler.sendResetLink(account.get());
            
            
            log.info("Reminded account: "+reminded.getLogin());
            return account2UserMap(reminded);
        } catch (UsersHandler.AccountHandlingException e) {
            log.error("Cannot remind {} {}",identifier,e.getMessage());
            throw new HandlingException(e.getMessage(),e);
        } catch(WebMappedException e) {
            log.error("Cannot remind account {}",identifier,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot remind account {}",identifier,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    } 
    
    
    @RequestMapping(value="reset",method = RequestMethod.POST)
    public Map<String,Object> reset(@NotNull @RequestBody Map<String,String> details,
            @NotNull @AuthenticationPrincipal BioDare2User user, 
            Authentication auth) {
        
        String token = details.getOrDefault("token", "");
        String password = details.getOrDefault("password", "");
        
        log.debug("reset account: {}; {}",token,user);
        
        
        
        
        try {
            BioDare2User reseted = usersHandler.resetPassword(password,token);
            tracker.userPasswordReset(reseted,user);
            securityTracker.userPasswordReset(reseted,auth);
            log.info("Reset password account: "+reseted.getLogin());
            
            return account2UserMap(reseted);
        } catch (UsersHandler.AccountHandlingException e) {
            try {
                //delay to prevent brute force
                Thread.sleep(500);
            } catch(InterruptedException ex){};
            log.error("Cannot reset {} {}",token,e.getMessage());
            throw new HandlingException(e.getMessage(),e);
        } catch(WebMappedException e) {
            log.error("Cannot reset account {}",token,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot reset account {}",token,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    } 
    
    public static Map<String,Object> account2UserMap(BioDare2User account) {
        Map<String,Object> user = new HashMap<>();
        
        user.put("login", account.getLogin());
        user.put("firstName", account.getFirstName());
        user.put("lastName", account.getLastName());
        user.put("email", account.getEmail());
        user.put("anonymous", account.isAnonymous());
        user.put("institution",account.getInstitution());
        return user;
    }    
}
