/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableImportParameters extends TSImportParameters implements Serializable {
    
    static final long serialVersionUID = 11L;
    
    public String fileName;
    public String fileId;
    public ImportFormat importFormat;
    public boolean inRows = false;

    public CellCoordinates firstTimeCell;
    public TimeType timeType;
    public double timeOffset = 0;
    public double imgInterval = 0;
    
    public CellCoordinates dataStart;

    public boolean importLabels = true;
    public CellCoordinates labelsSelection;   
    public List<String> userLabels = new ArrayList<>();
    
    public boolean containsBackgrounds = false;
    public List<String> backgroundsLabels = new ArrayList<>();
    
    public DataTableImportParameters transpose() {
        
        DataTableImportParameters transposed = new DataTableImportParameters();
        transposed.fileName = fileName;
        transposed.fileId = fileId;
        transposed.importFormat = importFormat;
        transposed.inRows = !inRows;
        
        transposed.firstTimeCell = firstTimeCell.transpose();
        transposed.timeType = timeType;
        transposed.timeOffset = timeOffset;
        transposed.imgInterval = imgInterval;
        
        transposed.dataStart = dataStart.transpose();
        transposed.importLabels = importLabels;
        transposed.labelsSelection = labelsSelection.transpose();
        transposed.userLabels = userLabels;
        
        transposed.containsBackgrounds = containsBackgrounds;
        transposed.backgroundsLabels = backgroundsLabels;
        
        return transposed;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.fileId);
        hash = 43 * hash + Objects.hashCode(this.importFormat);
        hash = 43 * hash + Objects.hashCode(this.firstTimeCell);
        hash = 43 * hash + Objects.hashCode(this.timeType);
        hash = 43 * hash + Objects.hashCode(this.dataStart);
        hash = 43 * hash + Objects.hashCode(this.labelsSelection);
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
        final DataTableImportParameters other = (DataTableImportParameters) obj;
        if (this.inRows != other.inRows) {
            return false;
        }
        if (Double.doubleToLongBits(this.timeOffset) != Double.doubleToLongBits(other.timeOffset)) {
            return false;
        }
        if (Double.doubleToLongBits(this.imgInterval) != Double.doubleToLongBits(other.imgInterval)) {
            return false;
        }
        if (this.importLabels != other.importLabels) {
            return false;
        }
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        if (!Objects.equals(this.fileId, other.fileId)) {
            return false;
        }
        if (this.importFormat != other.importFormat) {
            return false;
        }
        if (!Objects.equals(this.firstTimeCell, other.firstTimeCell)) {
            return false;
        }
        if (this.timeType != other.timeType) {
            return false;
        }
        if (!Objects.equals(this.dataStart, other.dataStart)) {
            return false;
        }
        if (!Objects.equals(this.labelsSelection, other.labelsSelection)) {
            return false;
        }
        if (!Objects.equals(this.userLabels, other.userLabels)) {
            return false;
        }        
        if (!Objects.equals(this.containsBackgrounds, other.containsBackgrounds)) {
            return false;
        }   
        if (!Objects.equals(this.backgroundsLabels, other.backgroundsLabels)) {
            return false;
        }           
        return true;
    }
    
    
}
