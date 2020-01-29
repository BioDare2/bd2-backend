/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare2.backend.repo.ui_dom.shared.Page;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableSlice {
    
  public List<String> columnsNames = new ArrayList<>();
  public List<Integer> columnsNumbers = new ArrayList<>();
  public List<String> rowsNames= new ArrayList<>();
  public List<Integer> rowsNumbers= new ArrayList<>();
  
  public List<List<Object>> data = new ArrayList<>();

  public int totalRows = 0;
  public int totalColumns = 0;
  
  public Page rowPage = new Page();
  public Page colPage = new Page();  
    
}
