/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class ExcelTSImportParameters extends TSImportParameters implements Serializable {
   
    static final long serialVersionUID = 11L;
 
  public CellRangeDescription timeColumn;
  public List<CellRangeDescription> dataBlocks = new ArrayList<>();    
}
