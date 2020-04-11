/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.tsdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ed.robust.dom.data.TimeSeries;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Zielu
 */
public class Trace {

  public int traceNr;  
  public long dataId; 
  public String traceRef;
  public String label;
  public boolean fill = false;
  public List<Timepoint> data = new ArrayList<>();
  public double min;
  public double max;
  public double mean;
  
  
  @JsonIgnore
  public void setTimeseries(TimeSeries series) {
      min = series.isEmpty() ? Double.NaN : series.getMinValue();
      max = series.isEmpty() ? Double.NaN : series.getMaxValue();
      mean = series.isEmpty() ? Double.NaN : series.getMeanValue();
      data.clear();
      series.forEach( tp -> data.add(new Timepoint(tp)) );
  }
}
