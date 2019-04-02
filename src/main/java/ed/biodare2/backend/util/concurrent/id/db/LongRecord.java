/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id.db;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 *
 * @author tzielins
 */
@Entity
public class LongRecord implements Serializable {
    private static final long serialVersionUID = 10L;

    @Id
    private String recordName;
    
    private long nextValue;
    
    @Version
    private long version;

    protected LongRecord() {
        
    }
    
    public LongRecord(String name,long nextValue) {
        this();
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        this.recordName = name;
        this.nextValue = nextValue;
    }
    
    public LongRecord(String name) {
        this(name,1);
    }

    public String getRecordName() {
        return recordName;
    }

    public long getNextValue() {
        return nextValue;
    }

    
    public long getVersion() {
        return version;
    }

    public void setNextValue(long nextValue) {
        this.nextValue = nextValue;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.recordName);
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
        final LongRecord other = (LongRecord) obj;
        if (!Objects.equals(this.recordName, other.recordName)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "LongRecord[" + recordName + "]";
    }
    
}
