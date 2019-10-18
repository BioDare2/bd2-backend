/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TranposableImportException extends ImportException {
    
    final String msg;
    final Integer row;
    final Integer col;
           
    public TranposableImportException(String msg, Integer row, Integer col) {
        super(msg);
        this.msg = msg;
        this.row = row;
        this.col = col;
    }
    
    public TranposableImportException(String msg, Integer row, Integer col, Throwable t) {
        super(msg, t);
        this.msg = msg;
        this.row = row;
        this.col = col;
    }
    
    public TranposableImportException transpose() {
        return new TranposableImportException(this.msg, this.col, this.row, this.getCause());
    }

    @Override
    public String getMessage() {
        String m = super.getMessage(); 
        if (m == null) m = "";
        String suf = " ";
        if (col != null) suf += " col:"+(col+1);
        if (row != null) suf += " row:"+(row+1);
        m += suf;
        return m.trim();
    }
    
    
    
}
