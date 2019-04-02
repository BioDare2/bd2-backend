/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.subscriptions;

import ed.biodare2.backend.security.dao.db.UserAccount;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tzielins
 */
@Entity
public class AccountSubscription implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    //@GeneratedValue(strategy = GenerationType.TABLE,generator="SubscriptionGen")
    //@TableGenerator(name="SubscriptionGen",allocationSize = 10, initialValue = 1000)
    private Long id;
    
    //@OneToOne(mappedBy = "kind",cascade=CascadeType.ALL)
    @OneToOne
    @MapsId()
    UserAccount account;
    
    @Version
    private long version;      
    
    @Enumerated(EnumType.ORDINAL)
    SubscriptionType kind = SubscriptionType.FREE;
    


    @NotNull
    //@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    LocalDateTime modifiedDate;            
    
    @NotNull
    //@Temporal(javax.persistence.TemporalType.DATE)
    LocalDate renewDate;    
    
    @NotNull
    //@Temporal(javax.persistence.TemporalType.DATE)
    LocalDate startDate;     

    protected long getVersion() {
        return version;
    }


    public SubscriptionType getKind() {
        return kind;
    }

    public void setKind(SubscriptionType subscription) {
        this.kind = subscription;
    }

    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }

    public LocalDate getRenewDate() {
        return renewDate;
    }

    public void setRenewDate(LocalDate renewDate) {
        this.renewDate = renewDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    
}
