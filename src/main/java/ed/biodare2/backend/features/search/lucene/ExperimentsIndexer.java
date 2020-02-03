/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import static ed.biodare2.backend.features.search.lucene.Fields.*;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.web.rest.ServerSideException;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import javax.annotation.PreDestroy;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentsIndexer implements AutoCloseable {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    final LuceneWriter writer;
    
    @Autowired
    public ExperimentsIndexer(LuceneWriter writer) {
        this.writer = writer;
    }
    
    @Override
    @PreDestroy
    public void close() throws Exception {
        
        log.info("Indexer closed");
    }
    
    
    public long indexExperiment(ExperimentalAssay exp, SystemInfo sysInfo) {
        
        try {
            Document doc = prepareDocument(exp, sysInfo);
            Term expIdTerm = new Term(EXP_ID,""+exp.getId());
            return writer.writeDocument(expIdTerm, doc);        
        } catch (IOException e) {
            throw new ServerSideException("Could not index exp "+exp.getId()+"; "+e.getMessage(),e);
        }
    }
    
    protected Document prepareDocument(ExperimentalAssay exp, SystemInfo sysInfo) {
        
        
        Document doc = new Document();
        
        doc.add(new StringField(EXP_ID, ""+exp.getId(),Field.Store.YES));
        // doc.add(new LongPoint(EXP_ID_S, exp.getId()));
        
        doc.add(new TextField(NAME, exp.getName(), Field.Store.NO));
        doc.add(new TextField(PURPOSE, exp.generalDesc.purpose, Field.Store.NO));
        doc.add(new TextField(DESCRIPTION, exp.generalDesc.description, Field.Store.NO));

        doc.add(new TextField(DATA_CATEGORY, exp.dataCategory.longName, Field.Store.NO));
        doc.add(new StringField(SPECIES, exp.species, Field.Store.NO));
        
        doc.add(new TextField(WHOLE_CONTENT, wholeContent(exp), Field.Store.NO));
        
        doc.add(new TextField(AUTHORS, authors(exp.contributionDesc), Field.Store.NO));
        
        doc.add(new LongPoint(UPLOADED, exp.provenance.created.toEpochSecond(ZoneOffset.UTC)));
        doc.add(new LongPoint(MODIFIED, exp.provenance.modified.toEpochSecond(ZoneOffset.UTC)));
        doc.add(new LongPoint(EXECUTED, exp.experimentalDetails.executionDate.toEpochSecond(LocalTime.NOON, ZoneOffset.UTC)));
        
        doc.add(new StringField(OWNER, sysInfo.security.owner, Field.Store.NO));
        doc.add(new StringField(IS_PUBLIC, ""+sysInfo.security.isPublic, Field.Store.NO));
        
        return doc;
    }

    protected String wholeContent(ExperimentalAssay exp) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(exp.getId()).append(" ");
        sb.append(exp.generalDesc.name).append(" ");
        sb.append(exp.generalDesc.purpose).append(" ");
        sb.append(exp.generalDesc.description).append(" ");
        sb.append(exp.generalDesc.comments).append(" ");
        
        exp.contributionDesc.authors.forEach( p -> sb.append(p.getName()).append(" "));
        exp.contributionDesc.curators.forEach( p -> sb.append(p.getName()).append(" "));
        exp.contributionDesc.institutions.forEach( p -> sb.append(p.longName).append(" "));
        exp.contributionDesc.fundings.forEach( p -> sb.append(p.institution.longName).append(" "));
        exp.contributionDesc.fundings.forEach( p -> sb.append(p.grantNr).append(" "));
        
        sb.append(exp.experimentalDetails.executionDate).append(" ");

        sb.append(exp.experimentalDetails.measurementDesc.technique).append(" ");
        sb.append(exp.experimentalDetails.measurementDesc.equipment).append(" ");
        exp.experimentalDetails.measurementDesc.parameters.parameters.values().forEach( v ->
                       sb.append(v.name).append(":").append(v.value).append(" "));
        sb.append(exp.experimentalDetails.measurementDesc.description).append(" ");

        sb.append(exp.dataCategory.longName).append(" ");
        sb.append(exp.species).append(" ");

        sb.append(exp.provenance.created).append(" ");
        sb.append(exp.provenance.modified).append(" ");

        return sb.toString();
    }

    protected String authors(ContributionDesc desc) {
        StringBuilder sb = new StringBuilder();
        desc.authors.forEach( p -> sb.append(p.getName()).append(" "));
        return sb.toString();
    }
    
}
