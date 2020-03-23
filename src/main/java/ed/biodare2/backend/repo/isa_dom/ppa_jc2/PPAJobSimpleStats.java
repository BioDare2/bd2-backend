/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
public class PPAJobSimpleStats {
  // it used to be long from JobCentre1   
  // public long jobId;
  
  public UUID jobId;
  
  public List<PPASimpleStats> stats = new ArrayList<>();
  
  protected PPAJobSimpleStats() {};
  
  /*PPAJobSimpleStats(long jobId) {
      this.jobId = jobId;
  };*/
  
  public PPAJobSimpleStats(UUID jobId) {
      this.jobId = jobId;
  };  
    
}
