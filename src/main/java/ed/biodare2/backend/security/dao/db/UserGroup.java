/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao.db;

import ed.biodare2.backend.security.BioDare2Group;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(indexes = {@Index(name="UserGroup_nameIX",columnList="name", unique = true)})
public class UserGroup implements BioDare2Group, Serializable {
  
    static final long serialVersionUID = 3L;

    public UserGroup() {}
    
    public static UserGroup testInstance(long id) {
        return new UserGroup(id);
    }
    
    private UserGroup(long id) {
        this();
        this.id = id;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="GroupGen")
    @TableGenerator(name="GroupGen",allocationSize = 10, initialValue = 1000)
    private Long id;
    @Version
    private long version;      

    protected String name;
    protected String longName;
    
    //protected boolean special;
    protected boolean system;
    
    @CreationTimestamp
    LocalDateTime creationDate;
    
    @UpdateTimestamp
    LocalDateTime modificationDate;
    

    /* Removed as not sure what should be the semantics if we have system groups and normal groups
    stored in two different tables. And probably there is no use for this method at the moment.
    @ManyToMany(mappedBy="groups",targetEntity = UserAccount.class)
    @OrderBy("login ASC")
    protected List<BioDare2User> members = new ArrayList<>();
    */
    @Override
    public Long getId() {
	return id;
    }




    @Override
    public String toString() {
	return "Group[" + id + "]="+name;
    }

    /*
    @Override
    public List<BioDare2User> getMembers() {
	return members;
    }*/

    /*
    @Override
    public void addMember(BioDare2User member) {
	getMembers().add(member);
    }*/

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void setName(String name) {
	this.name = name;
    }

    @Override
    public boolean isSystem() {
    	return system;
    }

    public void setSystem(boolean isSystem) {
        this.system = isSystem;
    }
    /*
    @Override
    public boolean isSpecial() {
    	return special;
    }*/

    @Override
    public String getLongName() {
        return longName;
    }

    @Override
    public void setLongName(String longName) {
        this.longName = longName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final UserGroup other = (UserGroup) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }


    
    
}
