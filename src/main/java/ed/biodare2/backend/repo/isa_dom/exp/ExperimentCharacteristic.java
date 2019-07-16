/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class ExperimentCharacteristic implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public boolean hasAttachments;
    public boolean hasTSData;    
    public boolean hasPPAJobs;     
    public boolean hasDataFiles;
    public long attachmentsSize;
    public long biodare1Id;
    public boolean isOpenAccess;
    public OpenAccessLicence licence;
    public boolean hasRhythmicityJobs;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.hasTSData ? 1 : 0);
        hash = 29 * hash + (this.hasPPAJobs ? 1 : 0);
        hash = 29 * hash + (this.hasDataFiles ? 1 : 0);
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
        final ExperimentCharacteristic other = (ExperimentCharacteristic) obj;
        if (this.hasTSData != other.hasTSData) {
            return false;
        }
        if (this.hasPPAJobs != other.hasPPAJobs) {
            return false;
        }
        if (this.hasDataFiles != other.hasDataFiles) {
            return false;
        }
        if (this.hasAttachments != other.hasAttachments) {
            return false;
        }
        if (this.attachmentsSize != other.attachmentsSize) {
            return false;
        }        
        if (this.biodare1Id != other.biodare1Id) {
            return false;
        }   
        if (this.isOpenAccess != other.isOpenAccess) {
            return false;
        }
        if (this.licence != other.licence) {
            return false;
        }
        if (this.hasRhythmicityJobs != other.hasRhythmicityJobs) {
            return false;
        }
        
        return true;
    }
    
    
}
