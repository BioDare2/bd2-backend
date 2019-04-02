/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.web.rest.HandlingException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public abstract class TableSimplifier {
    
    static final int DEF_NR_LENGTH = 8;
    static final int DEF_TOKEN_LENGTH = 10;
    static final int DEF_MAX_LENGTH = 30;
    
    static final String NON_BREAKIN_MINUS = Character.toString((char)8209);
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    
    public List<List<String>> simplify(Path file,int rows) throws IOException, HandlingException {
        return this.simplify(file, rows, DEF_TOKEN_LENGTH,DEF_MAX_LENGTH);
    }
    
    public List<List<String>> simplify(Path file,int rows,int tokenLength,int maxLength) throws IOException, HandlingException {
        
        List<List<String>> table = readTable(file, rows);
        
        table = simplifyTable(table,tokenLength,maxLength);
        
        table = padTable(table);
        
        return table;
    }
    
    protected abstract List<List<String>> readTable(Path file,int rows) throws IOException, HandlingException;
    
    protected List<List<String>> simplifyTable(List<List<String>> table,int tokenLength,int maxLength) {

        
        
        return table.stream().map( row -> simplifyRow(row,tokenLength,maxLength)).collect(Collectors.toList());
    }
    
    protected List<String> simplifyRow(List<String> org,int tokenLength,int maxLength) {
        
        return org.parallelStream().map( s -> simplify(s,tokenLength,maxLength)).collect(Collectors.toList());
    }
    
    protected String simplify(String str,int tokenLength,int maxLength) {
        
        if (str == null) return "";
        
        str = str.trim();
        
        try {
            double v = Double.parseDouble(str);
            return simplifyNumber(v);
        } catch (NumberFormatException e) {
            return simplifyStr(str,tokenLength,maxLength);

        }
    }
    
    protected String simplifyStr(String str,int tokenLength,int maxLength) {
        str = tokenize(str,tokenLength,maxLength);
        if (str.length() > maxLength) {
            str = str.substring(0,maxLength-2)+"..";
        }
        return str;
    }
    
    protected String tokenize(String str,int tokenLength,int maxLength) {
        if (str.length() <= tokenLength) return str;
        
        if (str.length() > maxLength+1) str = str.substring(0,maxLength+1);
        
        return Stream.of(str.split("\\s"))
                .flatMap( token -> {
                    if (token.length() <= tokenLength)
                        return Stream.of(token);
                    return Stream.of(token.substring(0,tokenLength),token.substring(tokenLength));
                })
                .collect(Collectors.joining(" "));
    }
    
    protected String simplifyNumber(double val) {
        
        
        if (isInteger(val)) {
            String str = Long.toString(Math.round(val));
            if (str.length() <= DEF_NR_LENGTH) return str;
        }
        
        val = simplifingRounding(val);        
        String str = Double.toString(val);
        if (str.length() <= DEF_NR_LENGTH) return str;
        
        return toScfString(val);
    }
    
    protected final boolean isInteger(double val) {
        return Math.rint(val) == val;
    }
    
    protected double simplifingRounding(double val) {
        double abs = Math.abs(val);
        if (abs < 1) return val;
        if (abs < 10000) {
            return Math.rint(val*100)/100.0;
        };
        return Math.rint(val);
    }
    
    protected String toScfString(double val) {
        
        if (val == 0) return "0";
        double abs = Math.abs(val);
        
        int exp = (int)Math.floor(Math.log10(abs));
        double pref = Math.rint(val*100/Math.pow(10, exp))/100;
        
        String str = Double.toString(pref)+"E"+(exp < 0 ? NON_BREAKIN_MINUS: "")+Math.abs(exp);

        /*
        if (exp < 0) {
            str = str.replace("-", NON_BREAKIN_MINUS);
            System.out.println(str);
        }*/
        return str;
        
    }

    protected List<List<String>> padTable(List<List<String>> table) {
        
        int width = table.stream().mapToInt( l -> l.size()).max().getAsInt();
        
        List<List<String>> res = new ArrayList<>(table.size());
        table.forEach( org -> {
            List<String> row = new ArrayList<>(org);
            while(row.size() < width) row.add("");
            res.add(row);
        });

        return res;
    }    
}
