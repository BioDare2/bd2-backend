/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class PPAJobResultsGroups {
  
  //it used to number in JC1  
  public long jobId;
  
  public double periodMin;
  public double periodMax;
  
  public List<PPAResultsGroupSummary> groups = new ArrayList<>();
  
  PPAJobResultsGroups() {};
  
  public PPAJobResultsGroups(long jobId) {
      this.jobId = jobId;
  };
  
}
