/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import ed.biodare2.EnvironmentVariables;
import java.nio.file.Path;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 *
 * @author tzielins
 */
@Configuration
// @Component
public class LuceneConfiguration {
    
    final static String INDEX_DIR = "index";
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    
    @Bean("luceneIndexDir")
    public Path luceneIndexDir(EnvironmentVariables environment) {
        Path indexDir = environment.storageDir.resolve(INDEX_DIR);
        log.info("LuceneConfiguration indexDir: {}", indexDir);
        return indexDir;
        
    }
    
}
