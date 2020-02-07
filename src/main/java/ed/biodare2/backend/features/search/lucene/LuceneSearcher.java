/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import static ed.biodare2.backend.features.search.lucene.Fields.ID;
import ed.biodare2.backend.web.rest.ListWrapper;
import ed.biodare2.backend.web.rest.ServerSideException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PreDestroy;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class LuceneSearcher implements AutoCloseable {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    // sensible number for sorting, it is current number of all biodare experiments
    // it will be double in a year, we could assume that queries that needs sorting should deal with 
    // half of the current number. 
    // if it is no longer true, lets make fancier search with pagind and sorting
    final static int MAX_HITS = 5_000;
    
    final SearcherManager searcherManager;

    @Autowired
    public LuceneSearcher( 
            /* Only so that the directory is initalized before searcher*/ 
            LuceneWriter writer) throws IOException {
        
        // log.info("Lucene search read uses index at: {}", indexDir);        
     
        // FSDirectory storage = configStorage(indexDir);
        // this.searcherManager = initManager(DirectoryReader.open(storage));
        this.searcherManager = initManager(writer.indexWriter);
    }
    
    /**
     * Just for testing 
     * @param indexReader
     */
    protected LuceneSearcher(DirectoryReader indexReader) throws IOException {
        this.searcherManager = initManager(indexReader);
    }
    

    public ListWrapper<Long> search(Query query,Optional<Sort> sort,int pageIndex, int pageSize) throws ServerSideException {
        try {
            return search(query, sort, pageIndex, pageSize, MAX_HITS);
        } catch (IOException e) {
            throw new ServerSideException("Cannot perform search: "+e.getMessage(),e);
        }
    }
    
    protected ListWrapper<Long> search(Query query,Optional<Sort> sort,int pageIndex, int pageSize, int maxHits) throws IOException {
        IndexSearcher searcher  = searcherManager.acquire();
        try {
            return search(searcher, query, sort, pageIndex, pageSize, maxHits);
        } finally {
          searcherManager.release(searcher);
        }        
    }
    
    protected ListWrapper<Long> search(IndexSearcher searcher, Query query, Optional<Sort> sort,int pageIndex, int pageSize, int maxHits) throws IOException {
        
        TopDocs hits = sort.isPresent() ? 
                search(searcher, query, sort.get(), maxHits) 
                : search(searcher, query, maxHits);

        int total = hits.scoreDocs.length;
        // reset page to zero if over the hits
        if (pageIndex*pageSize >= total) pageIndex = 0;

        List<Long> ids = extractIds(hits, pageIndex, pageSize, searcher);
        return new ListWrapper<>(ids, pageIndex, pageSize, total);
        
    }
    
    protected static SearcherManager initManager(IndexWriter writer) throws IOException {
        return new SearcherManager(writer, new SearcherFactory());
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
    

    /*
    protected static FSDirectory configStorage(Path indexDir) throws IOException {
        return FSDirectory.open(indexDir);        
    } */

    TopFieldDocs search(IndexSearcher searcher, Query query, Sort sort, int maxHits) throws IOException {
        TopFieldDocs hits = searcher.search(query, maxHits+1, sort);
        if (hits.scoreDocs.length > maxHits)
            throw new IllegalStateException("Number of matches exceeds the limit "+maxHits+" searching with sorting would not be acurate");
        
        return hits;
    }

    TopDocs search(IndexSearcher searcher, Query query, int maxHits) throws IOException {
        return searcher.search(query, maxHits);
    }

    List<Long> extractIds(TopDocs hits, int pageIndex, int pageSize, IndexSearcher searcher) throws IOException {
        
        final int start = pageIndex*pageSize;
        final int end = Math.min(start+pageSize, hits.scoreDocs.length);
        List<Long> ids = new ArrayList<>(pageSize);
        
        for (int i = start; i< end; i++) {
            Document doc = searcher.doc(hits.scoreDocs[i].doc);
            ids.add(doc.getField(ID).numericValue().longValue());
        }
        return ids;
    }


}
