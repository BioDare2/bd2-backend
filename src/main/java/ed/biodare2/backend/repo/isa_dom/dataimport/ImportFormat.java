/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum ImportFormat {
    
    NONE, //so the real values starts at 1
    EXCEL_TABLE,
    TOPCOUNT
    
}
