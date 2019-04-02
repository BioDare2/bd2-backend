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
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TableSimplifierTest {
    
    static class TestTableSimplifier extends TableSimplifier {

        @Override
        protected List<List<String>> readTable(Path file, int rows) throws IOException, HandlingException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public TableSimplifierTest() {
    }
    
    TableSimplifier instance;
    
    
    @Before
    public void setUp() {
        instance = new TestTableSimplifier();
    }

    @Test
    public void padTableAddsEmptyStringsToEqualizeRows() {
        List<List<String>> table = new ArrayList<>();
        table.add(Arrays.asList("1"));
        table.add(new ArrayList<>());
        table.add(Arrays.asList("1","2","3"));
        table.add(Arrays.asList("1","2"));
        
        List<List<String>> res = instance.padTable(table);
        assertEquals(table.size(),res.size());
        
        List<List<String>> exp = new ArrayList<>();
        exp.add(Arrays.asList("1","",""));
        exp.add(Arrays.asList("","",""));
        exp.add(Arrays.asList("1","2","3"));
        exp.add(Arrays.asList("1","2",""));        
    }
    
    @Test
    public void simplifyRowPreservesOrder() {

        List<String> org = new ArrayList<>();
        for (int i =0;i<10000;i++) org.add(""+i);
        
        List<String> res = instance.simplifyRow(org, 10,10);
        assertEquals(org,res);
    }
    
    @Test
    public void simplifyGivesCorrectlySimplifiedStrings() {
        
        String[] vals = {null,"","0.0","-1.011","a12345678","absd","abcdefgh","100.23789","-12300000.89"};
        
        String[] exps = {"",  "","0"  ,"-1.01","a123 4..","absd","abcd e..","100.24", "-1.23E7"};
        
        for (int i = 0;i<vals.length;i++) {
            String res = instance.simplify(vals[i],4,8);
            assertEquals(exps[i],res);
            
        }
        
    } 
    
    @Test
    public void simplifyStrShortensLongTexts() {
        int tokenLength = 3;
        int maxLength = 8;
        String[] vals = {"","ala","abba","abbabbacc"};
        
        String[] exps = {"", "ala","abb a"  ,"abb ab.."};        
        
        for (int i = 0;i<vals.length;i++) {
            String res = instance.simplifyStr(vals[i],tokenLength,maxLength);
            assertEquals(exps[i],res);
        }
    }
    
    @Test
    public void simplifyRoundingGivesSimplerDobules() {
        
        double[] vals = {0.0,-1.011,-0.001,10.1234,100.23789,9000.001,10000.89};
        double[] exps = {0.0,-1.01, -0.001,10.12,  100.24,   9000.00, 10001};
        
        for (int i = 0;i<vals.length;i++) {
            double res = instance.simplifingRounding(vals[i]);
            assertEquals(exps[i],res,1E-6);
            
        }
        
    }
    
    @Test
    public void toScfStringEncodesMinusInExpAsHyphen() {
        
        double[] vals = {-0.001};
        
        String[] exps = {"-1.0E‑3"};
        
        for (int i = 0;i<vals.length;i++) {
            String res = instance.toScfString(vals[i]);
            assertEquals(exps[i],res);
            
        }
        
    }
    
    
    @Test
    public void toScfStringGivesCorrectNotatoin() {
        
        double[] vals = {0.0,-1.011,   -0.001,   10.1234,  100.23789, 9078.001,-10000.89,-0.01569,12.356E14};
        
        String[] exps = {"0","-1.01E0","-1.0E‑3","1.01E1","1.0E2",  "9.08E3", "-1.0E4",  "-1.57E‑2","1.24E15"};
        
        for (int i = 0;i<vals.length;i++) {
            String res = instance.toScfString(vals[i]);
            assertEquals(exps[i],res);
            
        }
        
    }
    
    
}
