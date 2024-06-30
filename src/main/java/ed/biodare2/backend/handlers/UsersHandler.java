/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.services.mail.EmailChecker;
import ed.biodare2.backend.services.mail.Mailer;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.subscriptions.AccountSubscription;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.security.dao.UserTokenRep;
import ed.biodare2.backend.security.dao.db.UserToken;
import ed.biodare2.backend.security.dao.db.UserTokenKind;
import ed.biodare2.backend.web.rest.HandlingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author tzielins
 */
@Service
public class UsersHandler {

    final Logger log = LoggerFactory.getLogger(this.getClass());    
    
    public static final String currentTermsVersion = "1.0";
    
    static final int ACTIVATION_VALIDITY = 5;
    static final int RESET_VALIDITY = 1;
    static final int MIN_PASS = 8;
    
    final UserAccountRep users;
    final UserTokenRep tokens;
    final EmailChecker emailChecker;
    
    final Pattern only_small = Pattern.compile("\\p{javaLowerCase}+");
    final Pattern only_large = Pattern.compile("\\p{javaUpperCase}+");
    final Pattern only_digit = Pattern.compile("\\d+"); 
    final Pattern login_pattern = Pattern.compile("[a-z|0-9|_|\\.]+");
    
    
    
    final Validator validator;   
    final PasswordEncoder passwordEncoder; 
    final Mailer mailer;
    final RDMSocialHandler rdmSocialHandler;
    
    @Autowired
    public UsersHandler(UserAccountRep users,
            UserTokenRep tokens,
            @Qualifier("LocalValidator") Validator validator,
            PasswordEncoder passwordEncoder,
            Mailer mailer,
            EmailChecker emailChecker,
            RDMSocialHandler rdmSocialHandler
            ) {
        this.users = users;
        this.tokens = tokens;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        
        //this.tokenReader = mapper.readerFor(ActivationToken.class);
        //this.tokenWriter = mapper.writerFor(ActivationToken.class);
        //this.encryptor = encryptor;
        this.mailer = mailer;
        this.emailChecker = emailChecker;
        this.rdmSocialHandler = rdmSocialHandler;
        
        verifyValidator(validator);
        //log.info(this.getClass().getName()+" created");
        
    }
    
    private void verifyValidator(Validator validator) {
        UserAccount user = new UserAccount();
        Errors errors = new BindException(user,"user");
        validator.validate(user, errors);//,UserAccount.class);
        
        if (!errors.hasErrors()) {
            throw new IllegalStateException("Validator: "+validator.getClass().getName()+" does not correctly validates empty user");
        }        
    }
    
    @Transactional
    public BioDare2User register(Map<String, String> userDetails) throws AccountHandlingException {
        
        if (!userDetails.getOrDefault("terms", "").equals("true")) {
            throw new AccountHandlingException("Missing terms agreement");
        }
        
        UserAccount user = makeUser(userDetails);
        user.setRegistrationDate(LocalDate.now());
        validateNewUser(user);
        
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLocked(true);
        setSubscription(user);
        rdmSocialHandler.createUserAspect(user);
        
        //autoactivation
        //user.setLocked(false);
        //user.setActivationDate(LocalDate.now());        
        
        return users.save(user);
    }
    
    public Optional<BioDare2User> identify(String identifier) {
        
        identifier = identifier.toLowerCase().trim();
        List<UserAccount> hits = users.findByLoginOrEmailOrInitialEmail(identifier,identifier,identifier);
        
        if (hits.isEmpty()) return Optional.empty();        
        if (hits.size() != 1) throw new HandlingException("Mulitple users found for: "+identifier);
        return Optional.of(hits.get(0));
    }    
    
    @Transactional
    public BioDare2User update(Map<String, String> userDetails, BioDare2User invoker) throws AccountHandlingException {

        
        UserAccount details = makeUser(userDetails);
        
        if (!details.getLogin().equals(invoker.getLogin()))
            throw new AccountHandlingException("User can edit only its own account");
        
        UserAccount user = users.findByLogin(details.getLogin()).orElseThrow(() -> new NotFoundException("User "+details.getLogin()+" not found"));

        if (!passwordEncoder.matches(userDetails.getOrDefault("currentPassword",""),user.getPassword()))
             throw new AccountHandlingException("Wrong current password");
        
        // String newPassword = details.getPassword();
        // if (details.getPassword().isEmpty()) details.setPassword(user.getPassword());
        // details.setPassword(newPassword);
        
        // we do it like that so it can pass the user validation with no password set
        details.setPassword(user.getPassword());
        
        details.setInitialEmail(user.getInitialEmail());
        
        validateUser(details);
        
        
        updateDetails(details,user);
        
        return users.save(user);
        
    }

    @Transactional
    public BioDare2User updatePassword(Map<String, String> userDetails, BioDare2User invoker) throws AccountHandlingException {


        String login = userDetails.getOrDefault("login", "");
        if (!login.equals(invoker.getLogin()))
            throw new AccountHandlingException("User can edit only its own account");
        
        UserAccount user = users.findByLogin(login).orElseThrow(() -> new NotFoundException("User "+login+" not found"));

        if (!passwordEncoder.matches(userDetails.getOrDefault("currentPassword",""),user.getPassword()))
             throw new AccountHandlingException("Wrong current password");
        
        String newPassword = userDetails.getOrDefault("password","");
        
        if (newPassword.trim().isEmpty())
            throw new AccountHandlingException("Empty new password");
        
        checkPassword(newPassword);
        
        changePassword(newPassword,user);
        
        return users.save(user);
        
    }
    
    public EmailSuitability isSuitableEmail(String email)  {
        EmailSuitability suitability = new EmailSuitability();
        suitability.isAcademic = isAcademic(email);
        suitability.isFree = isUniqueInitialEmail(email);

        return suitability;
    }
    
    public boolean isAcademic(String email) {
        return emailChecker.isAcademic(email);
    }    
    
    protected void updateDetails(UserAccount details, UserAccount user) {
        
        user.setEmail(details.getEmail());
        user.setFirstName(details.getFirstName());
        user.setLastName(details.getLastName());
        user.setInstitution(details.getInstitution());
        
        /*if (!details.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(details.getPassword()));
        }*/
    }
  
    protected void changePassword(String newPassword, UserAccount user) {
        
        user.setPassword(passwordEncoder.encode(newPassword));
    }     
    
    protected void validateNewUser(UserAccount user) throws AccountHandlingException {
        
        validateUser(user);
        checkPassword(user.getPassword());
        checkAcademicEmail(user.getInitialEmail());
        checkNonBioDareLogin(user.getLogin());
        checkUniqueLogin(user.getLogin());
        checkUniqueEmail(user.getEmail(),user.getLogin());
        checkUniqueInitialEmail(user.getInitialEmail());
        
    }

    protected void validateUser(UserAccount user) throws AccountHandlingException {
        
        Errors errors = new BindException(user,"user");
        validator.validate(user, errors);//,UserAccount.class);
        
        if (errors.hasErrors()) {
            throw new AccountHandlingException(errorMessage(errors));
        }
        
        if (!login_pattern.matcher(user.getLogin()).matches())
            throw new AccountHandlingException("Non alphanumeric login: "+user.getLogin());
        
        // checkPassword(user.getPassword());
        
    }

    protected void checkUniqueLogin(String login) throws AccountHandlingException {
        
        if (users.findByLogin(login).isPresent())
            throw new AccountHandlingException("Duplicated login: "+login);
    }

    protected void checkNonBioDareLogin(String login) throws AccountHandlingException {
        
        if (login.startsWith("biodare"))
            throw new AccountHandlingException("login cannot start with biodare");
    }

    protected void checkUniqueEmail(String email,String login) throws AccountHandlingException {
        List<UserAccount> sameEmails = users.findByEmail(email);
        if (sameEmails.isEmpty()) return;
        for (UserAccount a: sameEmails) {
            if (!a.getLogin().equals(login))
                throw new AccountHandlingException("Duplicated email: "+email);
        }
        
    }

    protected boolean isUniqueInitialEmail(String email) {
        return users.findByInitialEmail(email).isEmpty();
    }
    
    protected void checkUniqueInitialEmail(String email) throws AccountHandlingException {
        if (!isUniqueInitialEmail(email))
            throw new AccountHandlingException("Duplicated initial email: "+email);
    }
    
    protected void checkAcademicEmail(String email) throws AccountHandlingException {
        if (!isAcademic(email))
            throw new AccountHandlingException("Non academic email: "+email);
    }    
    

    
    

    protected UserAccount makeUser(Map<String, String> userDetails) {
        
        UserAccount user = new UserAccount();
        user.setEmail(userDetails.getOrDefault("email", "").trim().toLowerCase());
        user.setInitialEmail(user.getEmail());
        user.setFirstName(userDetails.getOrDefault("firstName", "").trim());
        user.setLastName(userDetails.getOrDefault("lastName", "").trim());
        user.setLogin(userDetails.getOrDefault("login", "").trim().toLowerCase());
        user.setPassword(userDetails.getOrDefault("password", ""));
        user.setInstitution(userDetails.getOrDefault("institution", "").trim());
        
        user.setSupervisor(user);
        return user;
    }

    protected String errorMessage(Errors errors) {
        StringBuilder sb = new StringBuilder();
        sb.append("Not valid fields: ");
        sb.append(errors.getFieldErrors().stream()
                .map(f-> f.getField())
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.joining(",")));
                
        return sb.toString();
    }

    protected void checkPassword(String password) throws AccountHandlingException {
        if (password.length() < MIN_PASS)
            throw new AccountHandlingException("Password must be at least "+MIN_PASS+" long");
        
        if (only_digit.matcher(password).matches())
            throw new AccountHandlingException("Password must contains letter");
        
        if (only_small.matcher(password).matches())
            throw new AccountHandlingException("Password must contains a digit or a capital or a symbol");
        
        if (only_large.matcher(password).matches())
            throw new AccountHandlingException("Password must contains a digit or a small letter or a symbol");
    }

    protected void setSubscription(UserAccount user) {
        
        user.setTermsVersion(currentTermsVersion);
        
        AccountSubscription sub = new AccountSubscription();
        sub.setKind(SubscriptionType.FREE);
        sub.setStartDate(LocalDate.now());
        sub.setRenewDate(LocalDate.now().plusYears(1));
        
        user.setSubscription(sub);
    }

    @Transactional
    public BioDare2User activateAccount(String token) throws AccountHandlingException {
        
        UserToken details = tokens.findByToken(token)
                                .orElseThrow(() -> new AccountHandlingException("Already activated or activation token expired"));
        
                
        //ActivationToken details = decodeToken(token);
        if (!UserTokenKind.ACTIVATION.equals(details.getKind()))
            throw new AccountHandlingException("Wrong token type");
        if (details.getExpiring().isBefore(LocalDateTime.now()))
            throw new AccountHandlingException("Activation token expired");
        
        /*UserAccount user = users.findByLogin(details.login)
                    .orElseThrow(() -> new AccountHandlingException("Unknown user: "+details.login));
        
        if (!user.getEmail().equals(details.email))
            throw new AccountHandlingException("Activation email mismatch");
        */
        
        UserAccount user = (UserAccount)details.getUser();
        user.setLocked(false);
        user.setActivationDate(LocalDate.now());
        
        tokens.delete(details);
        return user;
    }
    
    @Transactional
    public BioDare2User resetPassword(String password, String token) throws AccountHandlingException {
        
        UserToken details = tokens.findByToken(token)
                                .orElseThrow(() -> new AccountHandlingException("Token already used or expired"));
        
        if (!UserTokenKind.PASSWORD_RESET.equals(details.getKind()))
            throw new AccountHandlingException("Wrong token type");
        if (details.getExpiring().isBefore(LocalDateTime.now()))
            throw new AccountHandlingException("Token expired");        
        
        checkPassword(password);
        
        UserAccount user = (UserAccount)details.getUser();
        user.setLocked(false);
        user.setFailedAttempts(0);
        user.setPassword(passwordEncoder.encode(password));
        if (user.getActivationDate() == null) user.setActivationDate(LocalDate.now());
        
        tokens.delete(details);
        return user;
    }    
    
    @Transactional
    public BioDare2User sendResetLink(BioDare2User user) throws AccountHandlingException {
        UserToken token = makeResetToken(user);
        token = tokens.saveAndFlush(token);
        
        String to = user.getEmail();
        String subject = "BioDare2 password reset";
        String body = 
                "You asked to reset the password for login: "+user.getLogin()+".\n"
                +"Please use the link below to reset your password:\n\n"
                ;
        String link = "https://biodare2.ed.ac.uk/account/reset?token="+token.getToken();
        body += "------------------\n"
                + link
                + "\n------------------\n"
                + "If the link does not work please copy the whole text between ----- to your browser "
                + "\n(it has to be one line no spaces so you may need to use an editor if your mail client scrambled it)"
                +"\n\n"
                + "All the best\nBioDare"
                ;
                
        if (!mailer.send(to, subject, body)) {
            throw new AccountHandlingException("Could not send rest email");
        }
        
        return user;
    }
    
    @Transactional
    public void sendActivationEmail(BioDare2User user) throws AccountHandlingException {
        UserToken token = makeActivationToken(user);
        token = tokens.saveAndFlush(token);
        
        String to = user.getEmail();
        String subject = "BioDare2 account activation";
        String body = 
                "Welcome to BioDare2\n\n"
                + "Your login is: "+user.getLogin()+"\n"
                + "Please use the link below to activate the account:\n\n"
                ;
        String link = "https://biodare2.ed.ac.uk/account/activate?token="+token.getToken();
        body += "------------------\n"
                + link
                + "\n------------------\n"
                + "If the link does not work please copy the whole text between ----- to your browser "
                + "\n(it has to be one line no spaces so you may need to use an editor if your mail client scrambled it)"
                +"\n\n"
                + "All the best\nBioDare"
                ;
                
        if (!mailer.send(to, subject, body)) {
            throw new AccountHandlingException("Could not send activation email");
        }
    }
    
    @Transactional
    @Scheduled(fixedRate = 1000*60*60*12, initialDelay = 1000*60*10)
    public void removeExpiredTokens() {
        
        List<UserToken> expired = tokens.findByExpiringBefore(LocalDateTime.now());
        log.info("Removing "+expired.size()+" expired tokens");
        
        tokens.deleteAll(expired);
        log.info("Deleted expired tokens");
    }

    protected UserToken makeActivationToken(BioDare2User user) {
    
        UserToken token = new UserToken(UUID.randomUUID().toString());
        token.setKind(UserTokenKind.ACTIVATION);
        token.setUser(user);
        token.setExpiring(LocalDateTime.now().plusDays(ACTIVATION_VALIDITY));
        return token;
    }
    
    protected UserToken makeResetToken(BioDare2User user) {
        UserToken token = new UserToken(UUID.randomUUID().toString());
        token.setKind(UserTokenKind.PASSWORD_RESET);
        token.setUser(user);
        token.setExpiring(LocalDateTime.now().plusDays(RESET_VALIDITY));
        return token;
    }    
    
    /*
    @Deprecated
    protected String getActivationToken(BioDare2User user) {
        
        ActivationToken token = new ActivationToken();
        token.login = user.getLogin();
        token.email = user.getEmail();
        
        return encodeToken(token);
    }

    @Deprecated
    protected String encodeToken(ActivationToken token) {

        try {
            String json = tokenWriter.writeValueAsString(token);
            return encryptor.encodeMsg(json);
        } catch (GeneralSecurityException| JsonProcessingException e) {
            throw new ServerSideException("Cannot create activation token: "+e.getMessage(),e);
        }
    }
    
    @Deprecated
    protected ActivationToken decodeToken(String msg) {
        try {
            msg = encryptor.decodeMsg(msg);
            return tokenReader.readValue(msg);
        } catch (GeneralSecurityException|IOException e) {
            throw new HandlingException("Cannot decode activation token",e);
        }
    }


    protected static class ActivationToken {
        public String login;
        public String email;
        public LocalDateTime expiration = LocalDateTime.now().plusDays(1);
        public double[] entropy = {Math.random(),Math.random(),Math.random(),Math.random(),Math.random()};
    }*/



   
    public static class AccountHandlingException extends Exception {
        public AccountHandlingException(String msg) {
            super(msg);
        }
        
        public AccountHandlingException(String msg,Throwable t) {
            super(msg,t);
        }
    }
    
    public static class EmailSuitability {
        public boolean isAcademic;
        public boolean isFree;
    }
}
