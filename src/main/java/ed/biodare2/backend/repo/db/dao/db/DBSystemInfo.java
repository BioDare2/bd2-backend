/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.db.dao.db;

import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author tzielins
 */
@Entity
@Table(indexes = {@Index(name="DBSystemInfo_parentIX",columnList="parent_id,entity_type", unique = true)})
public class DBSystemInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public DBSystemInfo() {};
    
    /**
     * For testing only
     * @param id 
     */
    private DBSystemInfo(Long id) {
        this.id = id;
    };
    
    public static DBSystemInfo testInstance(Long id) {
        return new DBSystemInfo(id);
    }
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="SystemInfoGen")
    @TableGenerator(name="SystemInfoGen", allocationSize = 10, initialValue = 1000,table = "hibernate_sequences")  
    private Long id;
    @Version
    private long version;      

    @NotNull
    @Column(name = "parent_id")
    long parentId;
    
    @NotNull
    @Column(name = "entity_type")
    EntityType entityType;
    
    
    @OneToOne(cascade=CascadeType.ALL,orphanRemoval = true)
    EntityACL acl;

    @OneToOne(cascade=CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    SearchInfo searchInfo;
    
    boolean deleted;
    
    @CreationTimestamp
    LocalDateTime creationDate;
    
    @UpdateTimestamp
    LocalDateTime modificationDate;
    
    @NotNull
    LocalDate embargoDate;
    
    
    public long getInnerId() {
        return id;
    }
    
    public long getParentId() {
        return parentId;
    }
    
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public EntityType getEntityType() {
        return entityType;
    }
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityACL getAcl() {
        return acl;
    }

    public void setAcl(EntityACL acl) {
        this.acl = acl;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    public LocalDate getEmbargoDate() {
        return embargoDate;
    }

    public void setEmbargoDate(LocalDate embargoDate) {
        this.embargoDate = embargoDate;
    }
    
    
    
}
