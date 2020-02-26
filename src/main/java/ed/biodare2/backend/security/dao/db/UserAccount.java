/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ed.biodare2.backend.security.BioDare2Group;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.features.rdmsocial.RDMUserAspect;
import ed.biodare2.backend.features.subscriptions.AccountSubscription;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Zielu
 */
@Entity
@Table(indexes = {
    @Index(name="UserAccount_loginIX",columnList="login", unique = true),
    @Index(name="UserAccount_initialEmailIX",columnList="initialemail", unique = false),
    @Index(name="UserAccount_emailIX",columnList="email", unique = false)
})
public class UserAccount implements Serializable, BioDare2User {
    
    static final long serialVersionUID = 3L;

    public UserAccount() {};
    
    public static UserAccount testInstance(long id) {
        return new UserAccount(id);
    }
    
    private UserAccount(long id) {
        this();
        this.id = id;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="UserGen")
    @TableGenerator(name="UserGen",allocationSize = 10, initialValue = 1000,table = "hibernate_sequences")
    private Long id;
    @Version
    private long version;      

    @NotNull
    @NotBlank @NotEmpty 
    @Size(min = 3)
    String login;
    
    @NotNull
    @NotBlank @NotEmpty 
    @Size(min = 2)
    String firstName;
    
    @NotNull
    @NotBlank @NotEmpty 
    @Size(min = 2)
    String lastName;
    
    @NotNull
    @NotBlank
    @NotEmpty @Email
    @Size(min = 4)
    String email;
    
    @NotNull
    @NotBlank
    @NotEmpty @Email
    @Size(min = 4)
    String initialEmail;    
    
    @NotNull
    @NotBlank @NotEmpty 
    @JsonIgnore            
    String password;
    
    String orcid;
    
    @NotBlank @NotEmpty
    String institution;
    
    String termsVersion;
    
    boolean PI = false;
    boolean asAdmin = false;
    boolean anonymous = false;
    boolean backendOnly = false;  
    
    @Column(name = "is_system")
    boolean system = false;
    
    boolean enabled = true;
    
    
    boolean expired = false;
    boolean credentialsExpired=false;    
    boolean locked = false; 
    boolean readOnly = false;
    int failedAttempts = 0;
    
    LocalDateTime lastLogin;
    String lastLoginAddress;
    
    LocalDate registrationDate;
    LocalDate activationDate;
    
    @CreationTimestamp
    LocalDateTime creationDate;
    
    @UpdateTimestamp
    LocalDateTime modificationDate;
    
    transient List<GrantedAuthority> authorities = new ArrayList<>();
    
    @ManyToOne(targetEntity = UserAccount.class,cascade = CascadeType.REFRESH,fetch = FetchType.EAGER)
    //@NotNull //was trigerring validation exception if set on itself
    @JoinTable(name="USERACCOUNT_SUPERVISOR")
    BioDare2User supervisor;
    
    //@ManyToMany(cascade=CascadeType.PERSIST,targetEntity = UserGroup.class)
    @ManyToMany(targetEntity = UserGroup.class,fetch = FetchType.EAGER)
    @OrderBy("name ASC")
    @JoinTable(name="USERACCOUNT_GROUP")
    protected Set<BioDare2Group> groups = new HashSet<>();
    
    @ManyToMany(targetEntity = UserGroup.class,fetch = FetchType.EAGER)
    @OrderBy("name ASC")
    @JoinTable(name="USERACCOUNT_SYSGROUP")
    protected Set<BioDare2Group> systemGroups = new HashSet<>();    
    
    @ManyToMany(targetEntity = UserGroup.class,fetch = FetchType.EAGER)
    @JoinTable(name="USERACCOUNT_DEFREAD")
    @OrderBy("name ASC")
    protected Set<BioDare2Group> defaultToRead = new HashSet<>();
    
    @ManyToMany(targetEntity = UserGroup.class,fetch = FetchType.EAGER)
    @JoinTable(name="USERACCOUNT_DEFWRITE")
    @OrderBy("name ASC")
    protected Set<BioDare2Group> defaultToWrite = new HashSet<>();  
    
    //@OneToOne(cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    //@PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    protected AccountSubscription subscription;
    
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    protected RDMUserAspect rdmAspect;
    
    @Transient
    protected boolean dirty;
    
    @Override
    final public boolean hasDirtySession() {
        return dirty;
    };
    
    @Override
    final public void setDirtySession(boolean dirty) {
        this.dirty = dirty;
    };

    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String getUsername() {
        return getLogin();
    }    
    
    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        if (this.initialEmail == null) this.initialEmail = email;
    }

    public String getInitialEmail() {
        return initialEmail;
    }

    public void setInitialEmail(String initialEmail) {
        this.initialEmail = initialEmail;
    }
    
    

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getORCID() {
        return orcid;
    }

    public void setORCID(String ORCID) {
        this.orcid = ORCID;
    }

    @Override
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    
    
    @Override
    public String getName() {
        return firstName+" "+lastName;
    }

    @Override
    public boolean isPI() {
        return PI;
    }
    
    public void setPI(boolean PI) {
        this.PI = PI;
    }  

    @Override
    public boolean isAdmin() {
        return asAdmin;
    }

    public void setAdmin(boolean admin) {
        this.asAdmin = admin;
    }
    
    
    
    @Override
    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean isBackendOnly() {
        return backendOnly;
    }

    public void setBackendOnly(boolean backendOnly) {
        this.backendOnly = backendOnly;
    }

    @Override
    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    
    
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    public void setAuthorities(List<GrantedAuthority> auth) {
        this.authorities = auth;
    }



    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserAccount other = (UserAccount) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }





    @Override
    public String toString() {
        return "User["+id+"]="+login;
    }

    @Override
    public BioDare2User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(BioDare2User supervisor) {
        this.supervisor = supervisor;
    }


    
    

    @Override
    public Set<BioDare2Group> getGroups() {
        
        return groups;
        /*
        return groups.stream()
                .filter( g -> !g.isSpecial())
                .filter( g -> !g.isSystem())
                .collect(Collectors.toList());
                */
    }
    
    @Override
    public Set<BioDare2Group> getSystemGroups() {
        
        return systemGroups;
        /*
        return groups.stream()
                .filter( g -> !g.isSpecial())
                .filter( g -> !g.isSystem())
                .collect(Collectors.toList());
                */
    }    

    /*
    @Override
    public List<BioDare2Group> getSystemGroups() {
        return groups.stream()
                .filter( g -> g.isSystem())
                .collect(Collectors.toList());  
    }*/
    
    /*
    @Override
    public List<BioDare2Group> getSpecialGroups() {
        return groups.stream()
                .filter( g -> g.isSpecial())
                .collect(Collectors.toList());  
    }*/

    @Override
    public void addGroup(BioDare2Group group) {
        if (group.isSystem()) systemGroups.add(group);
        else groups.add(group);
    }

    @Override
    public Set<BioDare2Group> getDefaultToRead() {
        return defaultToRead;
    }

    @Override
    public Set<BioDare2Group> getDefaultToWrite() {
        return defaultToWrite;
    }

    @Override
    public void addDefaultToRead(BioDare2Group group) {
        defaultToRead.add(group);
    }

    @Override
    public void addDefaultToWrite(BioDare2Group group) {
        defaultToWrite.add(group);
    }

    @Override
    public AccountSubscription getSubscription() {
        return subscription;
    }

    @Override
    public RDMUserAspect getRdmAspect() {
        return rdmAspect;
    }

    public void setRdmAspect(RDMUserAspect rdmAspect) {
        rdmAspect.setAccount(this);
        this.rdmAspect = rdmAspect;
    }
    
    

    public void setSubscription(AccountSubscription subscription) {
        subscription.setAccount(this);
        this.subscription = subscription;
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLoginAddress() {
        return lastLoginAddress;
    }

    public void setLastLoginAddress(String lastLoginAddress) {
        this.lastLoginAddress = lastLoginAddress;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }






    
    
    
}
