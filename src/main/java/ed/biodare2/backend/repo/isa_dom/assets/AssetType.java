/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AssetType {
    
    NONE,
    FILE,
    MEDIA,
    DATA,
    TS_DATA
    
}
