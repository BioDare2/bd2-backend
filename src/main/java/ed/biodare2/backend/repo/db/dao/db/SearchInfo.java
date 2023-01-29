/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.db.dao.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull
    LocalDateTime modificationDate;    
    
    @NotNull
    LocalDateTime executionDate;   
    
    LocalDateTime indexedDate;    
    
    @Column(length = 50, nullable = false)
    @NotNull
    String name;
    
    @Column(length = 25, nullable = false)
    @NotNull
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
