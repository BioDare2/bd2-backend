/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and isOpen the template in the editor.
 */
package ed.biodare2.backend.security.dao.db;

import ed.biodare2.backend.security.BioDare2Group;
import ed.biodare2.backend.security.BioDare2User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author tzielins
 */
@Entity
@Table(indexes = {
    @Index(name="DBSystemInfo_ownerIX",columnList="owner_id", unique = false),
    @Index(name="DBSystemInfo_superOwnerIX",columnList="super_owner_id", unique = false)
})
public class EntityACL implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public EntityACL() {};
    
    public static EntityACL testInstance(Long id) {
        return new EntityACL(id);
    }
    
    /**
     * For testing only;
     * @param id 
     */
    private EntityACL(Long id) {
        this();
        this.id = id;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="ACLGen")
    @TableGenerator(name="ACLGen",allocationSize = 10, initialValue = 1000,table = "hibernate_sequences")
    private Long id;
    @Version
    private long version;    

    @ManyToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    protected BioDare2User creator;
    
    @ManyToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    protected BioDare2User owner;

    @ManyToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    protected BioDare2User superOwner;
    
    protected boolean isOpen;
    
    @CreationTimestamp
    LocalDateTime creationDate;
    
    @UpdateTimestamp
    LocalDateTime modificationDate;
    

    @ManyToMany(targetEntity = UserGroup.class, fetch = FetchType.EAGER)
    @JoinTable(name="ENTITY_ACL_DBGROUP_READ",inverseJoinColumns = @JoinColumn(name="READ_GROUP_ID") )
    //@JoinTable(name="dbacl_dbgroup",inverseJoinColumns=@JoinColumn(name="allowedToRead_ID",columnDefinition="BIGINT NULL"))
    protected Set<BioDare2Group> allowedToRead = new HashSet<>();
    
    @ManyToMany(targetEntity = UserGroup.class, fetch = FetchType.EAGER)
    //@JoinTable(name="dbacl_dbgroup",inverseJoinColumns=@JoinColumn(name="allowedToWrite_ID",columnDefinition="BIGINT NULL"))
    @JoinTable(name="ENTITY_ACL_DBGROUP_WRITE",inverseJoinColumns = @JoinColumn(name="WRITE_GROUP_ID"))
    protected Set<BioDare2Group> allowedToWrite = new HashSet<>();

    public Long getId() {
	return id;
    }
    
 

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
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
        final EntityACL other = (EntityACL) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }


    public boolean isPublic() {
        return isOpen;
    }
    
    public void setPublic(boolean isPublic) {
        this.isOpen = isPublic;
    }

    public void clearLists()
    {
	getAllowedToRead().clear();
	getAllowedToWrite().clear();
    }

    public Set<BioDare2Group> getAllowedToRead() {
	return allowedToRead;
    }

    /*
    public <T extends Group> void setAllowedToRead(List<T> canRead) {
	this.allowedToRead = (List<DBGroup>) canRead;
    }*/

    public Set<BioDare2Group> getAllowedToWrite() {
	return allowedToWrite;
    }

    /*@SuppressWarnings("unchecked")
    public <T extends Group> void setAllowedToWrite(List<T> canWrite) {
	this.allowedToWrite = (List<DBGroup>) canWrite;
    }*/

    public void addCanRead(BioDare2Group group)
    {
	getAllowedToRead().add((UserGroup)group);
    }

    public void addCanWrite(BioDare2Group group)
    {
	getAllowedToWrite().add((UserGroup)group);
    }

    @Override
    public String toString() {
	return "ACL[id=" + id + "]";
    }


    public boolean canReadS(List<String> gNames)
    {
	for (String gName : gNames)
	    if (canRead(gName)) return true;
	return false;
    }

    public boolean canWriteS(List<String> gNames)
    {
	for (String gName : gNames)
	    if (canWrite(gName)) return true;
	return false;
    }

    public boolean canRead(List<BioDare2Group> groups)
    {
	for (BioDare2Group group: groups)
	    if (canRead(group)) return true;
	return false;
    }

    public boolean canWrite(List<BioDare2Group> groups)
    {
	for (BioDare2Group group: groups)
	    if (canWrite(group)) return true;
	return false;
    }

    public boolean canRead(BioDare2Group group)
    {
        return getAllowedToRead().contains(group);
    }

    public boolean canRead(String gName)
    {        
	return isInList(gName,getAllowedToRead());
    }

    public boolean canWrite(BioDare2Group group)
    {
        return getAllowedToWrite().contains(group);
    }

    public boolean canWrite(String gName)
    {
	return isInList(gName,getAllowedToWrite());
    }

    protected boolean  isInList(String name,Collection<BioDare2Group> groups)
    {
	for (BioDare2Group g : groups)
	    if (g.getName().equals(name)) return true;
	return false;
    }

    public BioDare2User getCreator() {
        return creator;
    }

    public void setCreator(BioDare2User creator) {
        this.creator = creator;
    }
    
    

    public BioDare2User getOwner() {
	return owner;
    }

    public void setOwner(BioDare2User owner) {
	this.owner = owner;
    }

    public BioDare2User getSuperOwner() {
	return superOwner;
    }

    public void setSuperOwner(BioDare2User superOwner) {
	this.superOwner = superOwner;
    }

    
}
