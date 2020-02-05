/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.PreDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class LuceneWriter implements AutoCloseable {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final IndexWriter indexWriter;

    @Autowired
    public LuceneWriter(@Qualifier("luceneIndexDir") Path indexDir) throws IOException {
        
        log.info("Lucene search writes uses index at: {}", indexDir.toAbsolutePath());        
     
        IndexWriterConfig config = configWriter(configAnalyser());
        
        FSDirectory storage = configStorage(indexDir);
        
        indexWriter = new IndexWriter(storage, config);
        
    }
    
    /**
     * Just for testing
     * @param indexWriter 
     */
    protected LuceneWriter(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }
    

    
    public long writeDocument(Term id, Document doc) throws IOException {
        indexWriter.updateDocument(id, doc);
        long resp = indexWriter.commit();
        return resp;
        // return 1;
    }
    
    public void deleteAll() throws IOException {
        indexWriter.deleteAll();
        indexWriter.commit();
    }    
    
    @Override
    @PreDestroy
    public void close() throws IOException {

        indexWriter.close();
        log.info("IndexWriter closed");
    }
    
    protected static Analyzer configAnalyser() {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        return analyzer;
    }
    
    protected static IndexWriterConfig configWriter(Analyzer analyser) {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyser);  
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriterConfig.setCommitOnClose(true);  
        return indexWriterConfig;
    }
    
    protected static FSDirectory configStorage(Path indexDir) throws IOException {
        return FSDirectory.open(indexDir);        
    } 




}
