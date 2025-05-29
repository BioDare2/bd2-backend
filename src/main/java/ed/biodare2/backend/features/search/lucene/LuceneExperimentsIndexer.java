/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import static ed.biodare2.backend.features.search.IndexingUtil.*;
import static ed.biodare2.backend.features.search.lucene.Fields.*;
import ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.PreDestroy;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class LuceneExperimentsIndexer implements AutoCloseable {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    final LuceneWriter writer;
    final LuceneSearcher searcher;
    
    @Autowired
    public LuceneExperimentsIndexer(LuceneWriter writer, LuceneSearcher searcher) {
        this.writer = writer;
        this.searcher = searcher;
    }
    
    @Override
    @PreDestroy
    public void close() throws Exception {
        
        writer.close();
        log.info("Indexer closed");
    }
    
    
    public long indexExperiment(ExperimentalAssay exp, SystemInfo sysInfo) {
        
        try {
            Document doc = prepareDocument(exp, sysInfo);
            Term expIdTerm = new Term(ID,""+exp.getId());
            long commit = writer.writeDocument(expIdTerm, doc);        
            searcher.updateIndex();
            return commit;
        } catch (IOException e) {
            throw new ServerSideException("Could not index exp "+exp.getId()+"; "+e.getMessage(),e);
        }
    }
    
    public long  indexExperiments(List<Pair<ExperimentalAssay, SystemInfo>> experiments)  {
        
        try {
            List<Pair<Term, Document>> docs = experiments.stream()
                .map( desc -> 
                        new Pair<>(new Term(ID,""+desc.getLeft().getId()), 
                                    prepareDocument(desc.getLeft(), desc.getRight())))
                .collect(Collectors.toList());
        
            long commit = writer.writeDocuments(docs);
            searcher.updateIndex();
            return commit;
        } catch (IOException e) {
            throw new ServerSideException("Could not index experimenst; "+e.getMessage(),e);
        }        
    }   
    
    
    public void clear() {
        try {
            writer.deleteAll();
            searcher.updateIndex();
        } catch (IOException e) {
            throw new ServerSideException("Could not clear index "+e.getMessage(),e);
        }    
    }    
    
    protected Document prepareDocument(ExperimentalAssay exp, SystemInfo sysInfo) {
        
        
        
        return prepareDocument(
                exp.getId(),
                exp.getName(),
                exp.generalDesc.purpose,
                exp.generalDesc.description,
                exp.dataCategory,
                exp.species,
                wholeContent(exp),
        
                author(exp.contributionDesc),
                authors(exp.contributionDesc),
        
                exp.provenance.created,
                exp.provenance.modified,
                exp.experimentalDetails.executionDate,
        
                sysInfo.security.owner,
                sysInfo.security.isPublic);
        
    }
    
    protected static Document prepareDocument(
            long id,
            String name,
            String purpose,
            String description,
            DataCategory dataCategory,
            String species,
            String wholeContent,
            String firstAuthor,
            String authors,
            LocalDateTime uploaded,
            LocalDateTime modified,
            LocalDate executed,
            String owner,
            boolean isPublic
    
    ) {
        
        
        Document doc = new Document();
        
        // for retrieval
        doc.add(new StoredField(ID, id));
        // for term queries == for updating
        doc.add(new StringField(ID, ""+id,Field.Store.NO));
        doc.add(new NumericDocValuesField(ID_S, id));
        
        doc.add(new TextField(NAME, name, Field.Store.NO));
        String shortName = name.length() > 50 ? name.substring(0,50) : name;
        shortName = shortName.toLowerCase();
        doc.add(new SortedDocValuesField(NAME_S, new BytesRef(shortName)));
        
        doc.add(new TextField(PURPOSE, purpose, Field.Store.NO));
        if (description != null)
            doc.add(new TextField(DESCRIPTION, description, Field.Store.NO));

        doc.add(new TextField(DATA_CATEGORY, dataCategory.longName, Field.Store.NO));
        doc.add(new StringField(SPECIES, species, Field.Store.NO));
        
        doc.add(new TextField(WHOLE_CONTENT, wholeContent, Field.Store.NO));
        
        doc.add(new StringField(FIRST_AUTHOR, firstAuthor, Field.Store.NO));
        doc.add(new SortedDocValuesField(FIRST_AUTHOR_S, new BytesRef(firstAuthor)));
        
        doc.add(new TextField(AUTHORS, authors, Field.Store.NO));
        
        doc.add(new LongPoint(UPLOADED, uploaded.toEpochSecond(ZoneOffset.UTC)));
        doc.add(new NumericDocValuesField(UPLOADED_S, uploaded.toEpochSecond(ZoneOffset.UTC)));
        
        doc.add(new LongPoint(MODIFIED, modified.toEpochSecond(ZoneOffset.UTC)));
        doc.add(new NumericDocValuesField(MODIFIED_S, modified.toEpochSecond(ZoneOffset.UTC)));

        doc.add(new LongPoint(EXECUTED, executed.toEpochSecond(LocalTime.NOON, ZoneOffset.UTC)));
        doc.add(new NumericDocValuesField(EXECUTED_S, executed.toEpochSecond(LocalTime.NOON, ZoneOffset.UTC)));
        
        
        doc.add(new StringField(OWNER, owner, Field.Store.NO));
        doc.add(new StringField(IS_PUBLIC, ""+isPublic, Field.Store.NO));
        
        return doc;
    }    








    
}
