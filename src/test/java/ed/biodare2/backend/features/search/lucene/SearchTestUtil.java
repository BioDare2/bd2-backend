/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author tzielins
 */
public class SearchTestUtil {
    
    
    public static List<Document> testDocuments() {
        
        List<Document> docs = new ArrayList<>();
        
        Document doc = LuceneExperimentsIndexer.prepareDocument(
                
                1, 
                "Name dFirst doc clock LHY", 
                "Purpose Testing search train", 
                "Description is what it is",
                DataCategory.GEN_IMAGING, 
                "Homo sapiens", 
                "Whole Content Purpose Testing search should contain the contan all but not Purpose Testing search Description is what it is", 
                "Zielinski2 Tomasz", 
                "Tomasz Zielinski Mark Smith", 
                LocalDateTime.now().minus(5, ChronoUnit.MONTHS), 
                LocalDateTime.now().minus(5, ChronoUnit.DAYS), 
                LocalDate.now().minus(5, ChronoUnit.WEEKS),
                "demo1", 
                false);
        
        docs.add(doc);
        
        doc = LuceneExperimentsIndexer.prepareDocument(
                
                2, 
                "Name ASecond doc clock TOC1", 
                "Purpose Testing clock search car", 
                "Description is what it is clock clock",
                DataCategory.EXPR_REPORTER, 
                "Homo sapiens", 
                "Whole Content Purpose Testing search should contain the contan all but not Purpose Testing search Description is what it is", 
                "Zielinski3 Tomasz", 
                "Tomasz Zielinski Andrew Millar", 
                LocalDateTime.now().minus(1, ChronoUnit.MONTHS), 
                LocalDateTime.now().minus(1, ChronoUnit.DAYS), 
                LocalDate.now().minus(3, ChronoUnit.WEEKS),
                "demo1", 
                true);
        
        docs.add(doc);  
        
        doc = LuceneExperimentsIndexer.prepareDocument(
                
                13, 
                "Name aThird doc PRR9", 
                "Purpose Testing search plane", 
                "Description is what it is",
                DataCategory.EXPR_REPORTER, 
                "Homo sapiens", 
                "Whole Content Purpose Testing search should contain the contan all but not Purpose Testing search Description is what it is", 
                "Zielinski1 Tomasz", 
                "Tomasz Zielinski Johnny Hay", 
                LocalDateTime.now().minus(2, ChronoUnit.MONTHS), 
                LocalDateTime.now().minus(2, ChronoUnit.DAYS), 
                LocalDate.now().minus(2, ChronoUnit.WEEKS),
                "demo1", 
                false);
        
        docs.add(doc);   
        
        doc = LuceneExperimentsIndexer.prepareDocument(
                
                25, 
                "Name BFith doc LHY", 
                "Purpose Testing search rocket", 
                "Description is what it is",
                DataCategory.EXPR_REPORTER, 
                "Mus musculus", 
                "Whole Content Purpose Testing search should contain the contan all but not Purpose Testing search Description is what it is", 
                "Darwin 1Charles", 
                "Charles Darwin Johnny Hay", 
                LocalDateTime.now().minus(4, ChronoUnit.MONTHS), 
                LocalDateTime.now().minus(3, ChronoUnit.DAYS), 
                LocalDate.now().minus(4, ChronoUnit.WEEKS),
                "demo2", 
                false);
        
        docs.add(doc);         

        doc = LuceneExperimentsIndexer.prepareDocument(
                
                14, 
                "Name CFourth doc PRR7", 
                "Purpose Testing search ship", 
                "Description is what it is clock",
                DataCategory.EXPR_REPORTER, 
                "Mus musculus", 
                "Whole Content Purpose Testing search should contain the contan all but not Purpose Testing search Description is what it is", 
                "Darwin 2Charles", 
                "Charles Darwin Johnny Hay", 
                LocalDateTime.now().minus(3, ChronoUnit.MONTHS), 
                LocalDateTime.now().minus(4, ChronoUnit.DAYS), 
                LocalDate.now().minus(1, ChronoUnit.WEEKS),
                "demo2", 
                true);
        
        docs.add(doc); 
        
        
        
        return docs;
        
    }
}
