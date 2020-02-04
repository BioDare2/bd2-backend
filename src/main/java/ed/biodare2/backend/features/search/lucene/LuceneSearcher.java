/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.PreDestroy;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class LuceneSearcher implements AutoCloseable {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final SearcherManager searcherManager;

    @Autowired
    public LuceneSearcher(@Value("luceneIndexDir") Path indexDir) throws IOException {
        
        log.info("Lucene search read uses index at: {}", indexDir);        
     
        FSDirectory storage = configStorage(indexDir);
        this.searcherManager = initManager(DirectoryReader.open(storage));
    }
    
    /**
     * Just for testing 
     */
    protected LuceneSearcher(DirectoryReader indexReader) throws IOException {
        this.searcherManager = initManager(indexReader);
    }
    

    protected static SearcherManager initManager(DirectoryReader indexReader) throws IOException {
        return new SearcherManager(indexReader, new SearcherFactory());
    }
    
    public void updateIndex() throws IOException {
        searcherManager.maybeRefresh();
    }
    
    @Override
    @PreDestroy
    public void close() throws IOException {

        searcherManager.close();
        log.info("IndexSearcher closed");
    }
    

    
    protected static FSDirectory configStorage(Path indexDir) throws IOException {
        return FSDirectory.open(indexDir);        
    } 


}
