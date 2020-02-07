/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.features.search.lucene.LuceneExperimentsIndexer;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.robust.dom.util.Pair;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentIndexer {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final LuceneExperimentsIndexer luceneExperimentsIndexer;

    @Autowired
    public ExperimentIndexer(LuceneExperimentsIndexer luceneExperimentsIndexer) {
        this.luceneExperimentsIndexer = luceneExperimentsIndexer;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void indexExperiment(AssayPack pack) {
        
        long sT = System.currentTimeMillis();
        
        log.info("Indexing {} owner {} public {}", pack.getId(), pack.getSystemInfo().security.owner, pack.getSystemInfo().security.isPublic);
        luceneExperimentsIndexer.indexExperiment(pack.getAssay(), pack.getSystemInfo());
        
        long dur = System.currentTimeMillis()-sT;
        
        log.info("INDEXING EXP {} took\t{}",pack.getId(), dur);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void indexExperiments(List<AssayPack> packs) {
        
        long sT = System.currentTimeMillis();
        
        log.info("Indexing experiments");
        
        List<Pair<ExperimentalAssay,SystemInfo>> experiments = packs.stream()
                .map( p -> new Pair<>(p.getAssay(), p.getSystemInfo()))
                .collect(Collectors.toList());
        
        luceneExperimentsIndexer.indexExperiments(experiments);
        
        long dur = System.currentTimeMillis()-sT;
        
        log.info("INDEXING EXPERIMENTS {} took\t{}",experiments.size(), dur);
        
    }    

    public void clear() {
        
        luceneExperimentsIndexer.clear();
    }
}
