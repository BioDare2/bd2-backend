/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import ed.biodare2.backend.security.dao.db.UserAccount;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Version;

/**
 *
 * @author Zielu
 */
@Entity
public class RDMUserAspect implements Serializable {
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
    RDMCohort cohort = RDMCohort.CONTROL;

    public Long getId() {
        return id;
    }

    
    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    public RDMCohort getCohort() {
        return cohort;
    }

    public void setCohort(RDMCohort cohort) {
        this.cohort = cohort;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.id);
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
        final RDMUserAspect other = (RDMUserAspect) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
}
