/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.UserGroupRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.security.dao.db.UserGroup;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.features.rdmsocial.RDMUserAspect;
import ed.biodare2.backend.features.subscriptions.AccountSubscription;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.security.core.authority.AuthorityUtils;
import static org.mockito.Mockito.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author tzielins
 */
public class Fixtures {
    
    public UserAccount systemUser;
    public UserAccount anonymous;
    public UserAccount demoUser;
    public UserAccount demoUser1;
    public UserAccount demoUser2;    
    public UserAccount demoBoss;
    public UserAccount user1;
    public UserAccount user2;
    public UserAccount admin;
    
    //public List<UserAccount> toSaveAccounts = new ArrayList<>();
    
    public UserGroup demoGroup;
    public UserGroup otherGroup;
    
    //public List<UserGroup> toSaveGroups = new ArrayList<>();
    
    static AtomicLong ids = new AtomicLong(LocalDateTime.now().getNano());
    
    public static Fixtures build() {
        UserAccountRep accounts = mock(UserAccountRep.class);
                when(accounts.save((UserAccount)any())).thenAnswer(new Answer<UserAccount>() {
            @Override
            public UserAccount answer(InvocationOnMock invocation) throws Throwable {
                UserAccount account = invocation.getArgument(0);
                if (account.getId() == null) {
                    account.setId(ids.getAndIncrement());
                }
                return account;
            }
        });  
        
        UserGroupRep groups = mock(UserGroupRep.class);
        when(groups.save((UserGroup)any())).thenAnswer(new Answer<UserGroup>() {
            @Override
            public UserGroup answer(InvocationOnMock invocation) throws Throwable {
                UserGroup group = invocation.getArgument(0);
                if (group.getId() == null) {
                    group.setId(ids.getAndIncrement());
                }
                return group;
            }
        });  
        
        return build(accounts,groups);
    }
    
    public static Fixtures build(UserAccountRep accounts,UserGroupRep groups) {
        PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
        
        return build(accounts, groups, passwordEncoder);
    }
    
    public static AccountSubscription makeSubsription() {
        return makeSubsription(SubscriptionType.FREE);
    }
    
    public static AccountSubscription makeSubsription(SubscriptionType kind) {
     AccountSubscription sub = new AccountSubscription();
     sub.setKind(kind);
     sub.setStartDate(LocalDate.now());
     sub.setRenewDate(LocalDate.now().plusYears(1));
     return sub;
    }    
    
    public static RDMUserAspect makeRDMAspect() {
        RDMUserAspect aspect = new RDMUserAspect();
        aspect.setCohort(RDMCohort.CONTROL);
        return aspect;
                
    }
    
    public static Fixtures build(UserAccountRep accounts,UserGroupRep groups,PasswordEncoder encoder) {
        
        final Logger log = LoggerFactory.getLogger(Fixtures.class);        
        log.warn("\n\nBuilding fixtures, id start: "+ids.get()+" encoder: "+encoder.getClass().getName());
        Fixtures fixtures = new Fixtures();
        
        UserGroup group;
        

        
        group = new UserGroup();
        group.setName("demo");
        group.setLongName("Demo Group");
        fixtures.demoGroup = groups.save(group);  
        
        group = new UserGroup();
        group.setName("group1");
        group.setLongName("Group One");
        fixtures.otherGroup = groups.save(group);        

        
        UserAccount acc;
        
        UserAccount sys;        
        sys = new UserAccount();
        sys.setLogin("system");
        sys.setFirstName("System");
        sys.setLastName("User");
        sys.setEmail("biodare1@ed.ac.uk");
        sys.setPassword(encoder.encode("system"));
        sys.setSupervisor(sys);
        sys.setSystem(true);
        sys.setBackendOnly(true);
        sys.setInstitution("BioDare");
        sys.setSubscription(makeSubsription());
        sys.getSubscription().setKind(SubscriptionType.SYSTEM);
        sys.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        sys.setRdmAspect(makeRDMAspect());
        fixtures.systemUser = accounts.save(sys);
        
        acc = new UserAccount();
        acc.setLogin("admin");
        acc.setFirstName("BioDare");
        acc.setLastName("Admin");
        acc.setEmail("biodare2@ed.ac.uk");
        acc.setPassword(encoder.encode("admin"));
        acc.setSupervisor(acc);
        acc.setInstitution("BioDare");
        acc.setAdmin(true);
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        fixtures.admin = accounts.save(acc);
        
        acc = new UserAccount();
        acc.setLogin("demoboss");
        acc.setFirstName("Demo");
        acc.setLastName("Boss");
        acc.setEmail("biodare3@ed.ac.uk");
        acc.setPassword(encoder.encode("demo"));
        acc.setSupervisor(acc);
        acc.setInstitution("University of Edinburgh");
        acc.addGroup(fixtures.demoGroup); 
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        fixtures.demoBoss = accounts.save(acc);

        acc = new UserAccount();
        acc.setLogin("demo");
        acc.setFirstName("Demo");
        acc.setLastName("User");
        acc.setEmail("biodare4@ed.ac.uk");
        acc.setPassword(encoder.encode("demo"));
        acc.setSupervisor(fixtures.demoBoss);
        acc.setInstitution("University of Edinburgh");
        acc.addGroup(fixtures.demoGroup); 
        acc.setSubscription(makeSubsription(SubscriptionType.EMBARGO_10));
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        
        fixtures.demoUser =accounts.save(acc);
        
        acc = new UserAccount();
        acc.setLogin("demo1");
        acc.setFirstName("Demo");
        acc.setLastName("User1");
        acc.setEmail("biodare5@ed.ac.uk");
        acc.setPassword(encoder.encode("demo"));
        acc.setSupervisor(fixtures.demoBoss);
        acc.setInstitution("University of Edinburgh");
        acc.addGroup(fixtures.demoGroup); 
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        fixtures.demoUser1 = accounts.save(acc);
        
        acc = new UserAccount();
        acc.setLogin("demo2");
        acc.setFirstName("Demo");
        acc.setLastName("User2");
        acc.setEmail("biodare52@ed.ac.uk");
        acc.setPassword(encoder.encode("demo"));
        acc.setSupervisor(fixtures.demoBoss);
        acc.setInstitution("University of Edinburgh");
        acc.addGroup(fixtures.demoGroup); 
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        fixtures.demoUser2 = accounts.save(acc);        

        acc = new UserAccount();
        acc.setLogin("user1");
        acc.setPassword(encoder.encode("user1"));
        acc.setFirstName("First");
        acc.setLastName("User");
        acc.setEmail("biodare6@ed.ac.uk");
        acc.setSupervisor(acc);
        acc.setInstitution("University of Edinburgh");
        acc.addGroup(fixtures.otherGroup);
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        fixtures.user1 = accounts.save(acc); 
        
        acc = new UserAccount();
        acc.setLogin("user2");
        acc.setPassword(encoder.encode("user2"));
        acc.setFirstName("Second");
        acc.setLastName("User");
        acc.setEmail("biodare62@ed.ac.uk");
        acc.setSupervisor(acc);
        acc.setInstitution("University of Edinburgh");
        acc.addGroup(fixtures.otherGroup);
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        fixtures.user2 = accounts.save(acc);         
        
        acc = new UserAccount();
        acc.setLogin("anonymous_1");
        acc.setFirstName("Anonymous");
        acc.setLastName("User");
        acc.setEmail("biodare7@ed.ac.uk");
        acc.setPassword(encoder.encode("demo"));
        acc.setSupervisor(sys);
        acc.setAnonymous(true);
        acc.setInstitution("University of Edinburgh");
        acc.setAuthorities(Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS","ROLE_READER","ROLE_USER")));
        acc.setSubscription(makeSubsription());
        acc.setTermsVersion(UsersHandler.CURRENT_TERMS_VERSION);
        acc.setRdmAspect(makeRDMAspect());
        
        fixtures.anonymous = acc;     

        groups.flush();
        accounts.flush();
        return fixtures;
    }
}
