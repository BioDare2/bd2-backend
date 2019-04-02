/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class UploadFileInfo {
    
    public String id;
    public String tmpFileName;
    public String originalFileName;
    public String contentType;
    public String uploadedBy;
    public LocalDateTime uploadedOn;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final UploadFileInfo other = (UploadFileInfo) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.tmpFileName, other.tmpFileName)) {
            return false;
        }
        if (!Objects.equals(this.originalFileName, other.originalFileName)) {
            return false;
        }
        if (!Objects.equals(this.contentType, other.contentType)) {
            return false;
        }
        if (!Objects.equals(this.uploadedBy, other.uploadedBy)) {
            return false;
        }
        if (!Objects.equals(this.uploadedOn, other.uploadedOn)) {
            return false;
        }
        return true;
    }
    
    
}
