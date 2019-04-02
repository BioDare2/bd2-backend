/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum OperationType {    
  NONE,
  CREATION,
  DATA_UPLOAD,
  FILE_UPLOAD,
  DESC_EDITION,
  DATA_EDITION,
  PPA_ANALYSIS,
  PPA_EDITION,
  DELETION,
  PUBLISH
}
