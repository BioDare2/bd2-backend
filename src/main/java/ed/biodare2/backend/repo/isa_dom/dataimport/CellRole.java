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
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CellRole {
  NONE, //so the real values starts at 1
  IGNORED, //1
  TIME,   //2
  DATA,   //3
  BACKGROUND, //4
  LABEL   //5    
  
}
