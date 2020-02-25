/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.db.dao.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;


/**
 *
 * @author tzielins
 */
@Entity
@Table(indexes = {
    @Index(name="SearchInfo_modificationDateIX",columnList="modificationDate", unique = false),
    @Index(name="SearchInfo_executionDateIX",columnList="executionDate", unique = false),
    @Index(name="SearchInfo_indexedDateIX",columnList="indexedDate", unique = false),
    @Index(name="SearchInfo_nameIX",columnList="name", unique = false),
    @Index(name="SearchInfo_firstAuthorIX",columnList="firstAuthor", unique = false)
})
public class SearchInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="SearchInfoGen")
    @TableGenerator(name="SearchInfoGen",allocationSize = 10, initialValue = 1000,table = "hibernate_sequences")
    private Long id;
    @Version
    private long version;   
    
    @CreationTimestamp
    LocalDateTime creationDate;
    
    LocalDateTime modificationDate;    
    
    LocalDateTime executionDate;   
    
    LocalDateTime indexedDate;    
    
    @Column(length = 50)
    String name;
    
    @Column(length = 25)
    String firstAuthor;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchInfo other = (SearchInfo) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public LocalDateTime getIndexedDate() {
        return indexedDate;
    }

    public void setIndexedDate(LocalDateTime indexedDate) {
        this.indexedDate = indexedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }
    
    
    
}
