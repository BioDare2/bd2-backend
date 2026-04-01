/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.Term;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import static ed.biodare2.backend.features.search.lucene.Fields.ID;
/**
 *
 * @author tzielins
 */
public class LuceneWriterTest {

    @TempDir
    Path testFolder;
    
    Path indexDir;
    public LuceneWriterTest() {
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        indexDir = testFolder.resolve("test");
    }

    @Test
    public void testCreatesUsingPath() throws Exception {
        
        try (LuceneWriter instance = new LuceneWriter(indexDir)) {
            assertNotNull(instance);  
            assertTrue(Files.list(indexDir).count() > 0);
        }
        
    }
    
    @Test
    public void updateCallsCommit() throws IOException {
        
        try (LuceneWriter instance = new LuceneWriter(indexDir)) {
            Document doc = new Document();
            doc.add(new StoredField(ID, 123));
            
            Term id = new Term(ID,"123");
            
            long resp = instance.writeDocument(id, doc);
            
            assertTrue(resp >= 0);
            resp = instance.indexWriter.commit();
            assertEquals(-1, resp);
        }
    }
}
