/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.security;

import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class SecuritySummary implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public boolean canRead;
    public boolean canWrite;
    public boolean isOwner;
    public boolean isSuperOwner;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.canRead ? 1 : 0);
        hash = 17 * hash + (this.canWrite ? 1 : 0);
        hash = 17 * hash + (this.isOwner ? 1 : 0);
        hash = 17 * hash + (this.isSuperOwner ? 1 : 0);
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
        final SecuritySummary other = (SecuritySummary) obj;
        if (this.canRead != other.canRead) {
            return false;
        }
        if (this.canWrite != other.canWrite) {
            return false;
        }
        if (this.isOwner != other.isOwner) {
            return false;
        }
        if (this.isSuperOwner != other.isSuperOwner) {
            return false;
        }
        return true;
    }
    
    
    
}
