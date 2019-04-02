/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class CellCoordinates implements Serializable {
   
    static final long serialVersionUID = 11L;
    
    public int col;
    public int row;

    protected CellCoordinates() {        
    }
    
    public CellCoordinates(int col,int row) {
        this.col = col;
        this.row = row;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.col;
        hash = 37 * hash + this.row;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellCoordinates other = (CellCoordinates) obj;
        if (this.col != other.col) {
            return false;
        }
        if (this.row != other.row) {
            return false;
        }
        return true;
    }

    public String columnLetter() {
        return colNrToExcelLetter(col);
    }
    
    
    static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    //1-based function
    public static String colNrToExcelLetter(int val) {
    
        String s = "";
        while (val > 0) {
            int r = (val -1) % LETTERS.length;
            s = LETTERS[r] + s;
            val = (val - r) / LETTERS.length;
        }
        return s;
    }    
    
    public static String colNrToExcelLetter2(int col) {

        col = col-1;
        int S = LETTERS.length;
        if (col < S) return Character.toString(LETTERS[col]);

        if (col < (S*S+S)) {
            int f = col/S - 1;
            int r = col % S;
        
            return Character.toString(LETTERS[f])+Character.toString(LETTERS[r]);
        }

        System.out.println("C: "+col+"; "+(S*S));
        if (col < 16384) {
            int lf = col/(S*S+S) - 1;
            System.out.println("LF: "+lf);
            int r = col % (S*S+S);
            System.out.println("r1: "+r);
            int f = r/S;
            System.out.println("F: "+f);
            r = col % S;
            System.out.println("r2: "+r);
            return Character.toString(LETTERS[lf])+Character.toString(LETTERS[f])+Character.toString(LETTERS[r]);
        }
        
        throw new IllegalArgumentException("Unsuported column number: "+(col+1));
    }   
    
    

}
