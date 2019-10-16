/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import java.io.Serializable;

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
}
