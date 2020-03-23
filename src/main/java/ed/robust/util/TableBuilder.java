/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.util;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class TableBuilder {
    
    final StringBuilder sb;
    final String SEP;
    final String ESC;
    final DateFormat dateFormat;
    
    public TableBuilder() {
        this(",", "\"");
    }
    
    public TableBuilder(String sep,String esc) {
        
        List<String> forbiden = Arrays.asList(".","-",":"," ");
        if (sep == null || esc == null)
            throw new IllegalArgumentException("Separator and Escape cannot be null");
        if (forbiden.contains(sep) || forbiden.contains(esc))
            throw new IllegalArgumentException("Separator and Escape cannot contain reserved characeters: "+forbiden);
        if (sep.equals(esc) || sep.contains(esc))
            throw new IllegalArgumentException("Separator and Escape cannot overlap");
        this.SEP = sep;
        this.ESC = esc;
        sb = new StringBuilder();
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    }
    
    
   public TableBuilder printlnVal(String value) {
	return printVal(value).endln();
    }

   public TableBuilder printlnVal(Double value) {
	return printVal(value).endln();
    }

   public TableBuilder  printVal(String value) {
        if (value == null) {
            sb.append("NULL");
        } else {
            if (value.contains(SEP)) sb.append(escape(value));
            else sb.append(value);
        }
       
	sb.append(SEP);
        return this;
    }

   public TableBuilder  printVal(Long value) {
	sb.append(value).append(SEP);
        return this;
    }
   
   public TableBuilder  printVal(Double value) {
	sb.append(value).append(SEP);
        return this;
    }

   public TableBuilder  printVal(Integer value) {
	sb.append(value).append(SEP);
        return this;
    }
   
   public TableBuilder printVal(Date value) {
       return printVal(dateFormat.format(value));
   }
   
   public TableBuilder printVal(LocalDateTime value) {
       return printVal(value != null ? value.toString() : "null");
   }
   

   public TableBuilder  printLabel(String label) {
        if (label == null) label = "";
        if (label.contains(SEP)) sb.append(escape(label));
        else sb.append(label);
        
	sb.append(SEP);
        
        return this;
   }
   
   protected String escape(String str) {
       return ESC+str+ESC;
   }

  public TableBuilder  printlnLabel(String label) {
	return printLabel(label).endln();
    }

   public TableBuilder  printlnParam(String label,String value) {
        return printParam(label,value).endln();
   }

   public TableBuilder  printlnParam(String label,double value) {
        return printParam(label,value).endln();
   }

   public TableBuilder  printlnParam(String label,int value) {
	return printParam(label,value).endln();
    }

   public TableBuilder  printlnParam(String label,Date value) {
	return printParam(label,value).endln();
    }
   
   public TableBuilder  printlnParam(String label,LocalDateTime value) {
	return printParam(label,value).endln();
    }   

   public TableBuilder  printParam(String label,String value) {
        return printLabel(label).printVal(value);
   }

   public TableBuilder  printParam(String label,double value) {
	return printLabel(label).printVal(value);
    }

   public TableBuilder printParam(String label,int value) {
       return printLabel(label).printVal(value);
    }

    public TableBuilder printParam(String label,Date value) {
       return printLabel(label).printVal(value);
    }
    
    public TableBuilder printParam(String label,LocalDateTime value) {
       return printLabel(label).printVal(value);
    }    

    public TableBuilder endln() {
        sb.append("\n");
        return this;
    }
    
    @Override
    public String toString() {
        return sb.toString();
    }
    
    
}
