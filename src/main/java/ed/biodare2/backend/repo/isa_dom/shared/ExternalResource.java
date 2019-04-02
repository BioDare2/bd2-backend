/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.shared;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class ExternalResource implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public final String externalId;
    public final String path;
    public final String service;

    public ExternalResource(String externalId, String path, String service, String version) {
        this.externalId = externalId;
        this.path = path;
        this.service = service;
    }

    @Override
    public String toString() {
        return externalId + "[" + service + ']';
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.externalId);
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
        final ExternalResource other = (ExternalResource) obj;
        if (!Objects.equals(this.externalId, other.externalId)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.service, other.service)) {
            return false;
        }

        return true;
    }
    
    
}
