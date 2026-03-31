/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class CellCoordinatesTest {
    
    public CellCoordinatesTest() {
    }

    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        CellCoordinates org = DomRepoTestBuilder.makeCellCoordinates();
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        CellCoordinates cpy = mapper.readValue(json, CellCoordinates.class);        
        assertEquals(org.col,cpy.col);
        assertEquals(org.row,cpy.row);
    }
    
    @Test
    public void readsUIJSON() throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = "{\"col\":1,\"row\":3}";
        
        CellCoordinates cpy = mapper.readValue(json, CellCoordinates.class);
        assertEquals(1,cpy.col);
        assertEquals(3,cpy.row);        
    }
    
    @Test
    public void convertsColumnNumberToLetters() {
        
        int[] numbers =    {1,  26,  27,  52,  53, 702, 703,   796, 16384};
        String[] letters = {"A","Z","AA","AZ","BA","ZZ","AAA","ADP", "XFD"};
        
        for (int i=0;i<numbers.length;i++) {
            String res = CellCoordinates.colNrToExcelLetter(numbers[i]);
            assertEquals(letters[i], res);
        }
    }
    
    @Test
    public void convertsColumnNumberToLettersForAllUpTo10000() {
        
        for (int i=1;i<2000;i++) {
            try {
                String res = CellCoordinates.colNrToExcelLetter(i);
                //System.out.println(res);
            } catch (Exception e) {
                System.out.println("Failed at: "+i);
                throw e;
            }
        }
    }    
    
    @Test
    public void convertsColumnNumberToLettersForDifficult() {
        
        int i = 1353;
        i = 1378;
        try {
            String res = CellCoordinates.colNrToExcelLetter(i);
            //System.out.println(i+":"+res);
        } catch (Exception e) {
            throw new RuntimeException("Failed at: "+i+": "+e.getMessage(),e);
        }
        i = 1379;
        try {
            String res = CellCoordinates.colNrToExcelLetter(i);
            //System.out.println(i+":"+res);
        } catch (Exception e) {
            throw new RuntimeException("Failed at: "+i+": "+e.getMessage(),e);
        }
        i = 701;
        try {
            String res = CellCoordinates.colNrToExcelLetter(i);
            //System.out.println(i+":"+res);
        } catch (Exception e) {
            throw new RuntimeException("Failed at: "+i+": "+e.getMessage(),e);
        }
        i = 702;
        try {
            String res = CellCoordinates.colNrToExcelLetter(i);
            //System.out.println(i+":"+res);
        } catch (Exception e) {
            throw new RuntimeException("Failed at: "+i+": "+e.getMessage(),e);
        }
        i = 703;
        try {
            String res = CellCoordinates.colNrToExcelLetter(i);
            //System.out.println(i+":"+res);
        } catch (Exception e) {
            throw new RuntimeException("Failed at: "+i+": "+e.getMessage(),e);
        }
        i = 704;
        try {
            String res = CellCoordinates.colNrToExcelLetter(i);
            //System.out.println(i+":"+res);
        } catch (Exception e) {
            throw new RuntimeException("Failed at: "+i+": "+e.getMessage(),e);
        }
        
    }     
}
