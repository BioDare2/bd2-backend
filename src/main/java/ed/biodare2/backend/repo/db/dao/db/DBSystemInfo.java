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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
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
    @TableGenerator(name="SystemInfoGen", allocationSize = 10, initialValue = 1000)  
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
    
    boolean deleted;
    
    @CreationTimestamp
    LocalDateTime creationDate;
    
    @UpdateTimestamp
    LocalDateTime modificationDate;
    
    
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
    
    
    
}
