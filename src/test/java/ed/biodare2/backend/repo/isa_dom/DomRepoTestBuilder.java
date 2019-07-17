/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom;

import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import ed.biodare2.backend.repo.isa_dom.measure.MeasurementDesc;
import ed.biodare2.backend.repo.isa_dom.actors.Institution;
import ed.biodare2.backend.repo.isa_dom.actors.Person;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalDescription;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalInfo;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalInfoBuilder;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologySummary;
import ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory;
import ed.biodare2.backend.repo.isa_dom.conditions.Environment;
import ed.biodare2.backend.repo.isa_dom.conditions.Environments;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.contribution.Funding;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalDetails;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.param.Parameter;
import ed.biodare2.backend.repo.isa_dom.param.FullParameters;
import ed.biodare2.backend.repo.isa_dom.param.FullParametersTest;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import static ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary.*;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import ed.biodare2.backend.repo.ui_dom.security.SecuritySummary;
import ed.robust.dom.data.DetrendingType;

import ed.robust.ppa.PPAMethod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author tzielins
 */
public class DomRepoTestBuilder {

    static AtomicLong ids = new AtomicLong(2000);
    
    /*public static ExperimentAssay makeExperiment(long id) {
        ExperimentAssay exp = new ExperimentAssay(id);
        exp.contributionDesc = makeContributionDesc();
        exp.experimentalEnvironments = makeEnvironments();
        exp.growthEnvironments = makeEnvironments();
        exp.generalDesc = makeGeneralDesc();
        exp.measurementDesc = makeMeasurementDesc();
        exp.provenance = makeSimpleProvenance();
        return exp;
    }*/

    public static ExperimentalAssay makeExperimentalAssay() {
        ExperimentalAssay assay = new ExperimentalAssay(ids.incrementAndGet());
        assay.generalDesc = makeGeneralDesc();
        assay.contributionDesc = makeContributionDesc();
        assay.experimentalDetails = makeExperimentalDetails();
        assay.characteristic = new ExperimentCharacteristic();
        assay.bioDescription = makeBiologicalDesription();
        assay.bioSummary = new BiologySummary(assay.bioDescription);
        assay.provenance = makeSimpleProvenance();
        assay.species = assay.bioDescription.bios.stream()
                                .map(d -> d.species).findAny().orElse("Arabidopsis thaliana");
        assay.dataCategory = assay.bioDescription.bios.stream()
                                .map(d -> d.dataCategory).findAny().orElse(DataCategory.GEN_IMAGING);
        
        return assay;
    }     
    
    public static ExperimentalAssayView makeExperimentalAssayView() {
        ExperimentalAssay assay = makeExperimentalAssay();
        ExperimentalAssayView view = new ExperimentalAssayView(assay);
        view.security = makeSecuritySummary();
        view.species = "Arabidopsis";
        view.dataCategory = DataCategory.GEN_IMAGING;
        return view;
    }     
    
    
    public static ExperimentalDetails makeExperimentalDetails() {
        ExperimentalDetails exp = new ExperimentalDetails();
        exp.experimentalEnvironments = makeEnvironments();
        exp.growthEnvironments = makeEnvironments();
        exp.measurementDesc = makeMeasurementDesc();
        exp.executionDate = LocalDate.now();
        return exp;
    }    

    public static GeneralDesc makeGeneralDesc() {
        GeneralDesc desc = new GeneralDesc();
        desc.name = "Test experiment";
        desc.purpose = "To check code";
        desc.comments = "A commment";
        desc.description = "A description";
        return desc;
    }

    public static ContributionDesc makeContributionDesc() {
        ContributionDesc desc = new ContributionDesc();
        desc.authors.add(makePerson("test"));
        desc.curators.add(makePerson("curator"));
        desc.fundings.add(makeFunding("UoE","123"));
        desc.institutions.add(makeInstitution("Cambridge"));
        Institution inst = makeInstitution("Fife");
        inst.address = null;
        inst.longName = null;
        desc.institutions.add(inst);
        return desc;
    }

    public static Environments makeEnvironments() {
        Environments envs = new Environments();
        Environment env = new Environment();
        env.name = "LL";
        env.description = "Desc";
        envs.environments.add(env);
        env = new Environment();
        env.name = "LD";
        envs.environments.add(env);
        return envs;
    }

    public static MeasurementDesc makeMeasurementDesc() {
        MeasurementDesc desc = new MeasurementDesc();
        desc.technique = "Luciferase luminescence";
        desc.equipment = "Topcount 1";
        desc.parameters = makeParameters();
        desc.description = " A description";
        return desc;
    }

    public static Person makePerson(String login) {
        Person org = new Person();
        org.login = login;
        org.id = ids.incrementAndGet();
        org.firstName = "First"+login;
        org.lastName = "Last"+login;
        //org.externalPath = "biodare/account/"+login;
        //org.externalService = "biodare2";
        return org;
    }

    public static Funding makeFunding(String instName,String grant) {
        Institution inst = makeInstitution(instName);
        Funding org = new Funding();
        org.institution = inst;
        org.grantNr = grant;
        return org;
    }

    public static Institution makeInstitution(String name) {
        Institution org = new Institution();
        org.id = ids.incrementAndGet();
        org.name = name;
        org.address = name + " in Edinburgh";
        org.longName = "University " + name;
        org.web = "www." + name + ".ed.ac";
        return org;
    }

    public static FullParameters makeParameters() {
        FullParameters params = new FullParameters();
        Parameter p;
        p = new Parameter("first", "2", "first param", "a unit");
        params.parameters.put(p.name, p);
        p = new Parameter("second", "3", "2n param", null);
        params.parameters.put(p.name, p);
        p = new Parameter("empty");
        params.parameters.put(p.name, p);
        p = new Parameter("last", "a value", null, null);
        params.parameters.put(p.name, p);
        return params;
    }

    public static CellCoordinates makeCellCoordinates() {
        CellCoordinates coord = new CellCoordinates(2,3);
        return coord;
    }

    public static CellRange makeCellRange() {
        CellRange range = new CellRange();
        range.first = makeCellCoordinates();
        range.last = makeCellCoordinates();
        range.last.col = 5;
        return range;
    }

    public static CellRangeDescription makeCellRangeDescription() {
        CellRangeDescription dsc = new CellRangeDescription();
        dsc.range = makeCellRange();
        dsc.role = CellRole.DATA;
        DataColumnProperties prop = new DataColumnProperties();
        prop.dataLabel = "TOC 1";
        dsc.details = prop;
        return dsc;
    }

    public static ExcelTSImportParameters makeExcelTSImportParameters() {
        ExcelTSImportParameters params = new ExcelTSImportParameters();
        params.timeColumn = makeTimeColumn();
        params.dataBlocks.add(params.timeColumn);
        params.dataBlocks.add(makeCellRangeDescription());
        params.dataBlocks.get(0).role = CellRole.TIME;
        params.dataBlocks.add(makeCellRangeDescription());
        return params;
    }
    
    public static CellRangeDescription makeTimeColumn() {
        CellRangeDescription dsc = new CellRangeDescription();
        
        CellCoordinates cell = new CellCoordinates(1,2);
        
        dsc.range = new CellRange();
        dsc.range.first = cell;
        dsc.range.last = cell;
        
        dsc.role = CellRole.TIME;
        dsc.details = makeTimeColumnProperties();
        return dsc;        
    }

    public static TimeColumnProperties makeTimeColumnProperties() {
        TimeColumnProperties params = new TimeColumnProperties();
        params.firstRow = 2;
        params.timeType = TimeType.TIME_IN_HOURS;
        params.timeOffset = 3;
        return params;
    }

    public static PPARequest makePPARequest() {
        PPARequest req = new PPARequest();
        req.windowStart = 1;
        req.windowEnd = 100;
        req.periodMin = 18;
        req.periodMax = 35;
        req.detrending = DetrendingType.POLY_DTR;
        req.method = PPAMethod.MESA;
        return req;
    }
    
    public static RhythmicityRequest makeRhythmicityRequest() {
        RhythmicityRequest req = new RhythmicityRequest();
        req.windowStart = 1;
        req.windowEnd = 100;
        req.periodMin = 24;
        req.periodMax = 24;
        req.detrending = DetrendingType.POLY_DTR;
        req.method = "BD2EJTK";
        req.preset = "BD2_CLASSIC";
        return req;
    }  
    
    public static RhythmicityJobSummary makeRhythmicityJobSummary(UUID jobId, long expId) {
        
        RhythmicityJobSummary job = new RhythmicityJobSummary();
        job.jobId = jobId;
        job.jobStatus = new JobStatus(jobId, State.SUBMITTED);
        job.parameters = new HashMap<>();
        
	job.parameters.put(DW_START, "0");
	job.parameters.put(DW_END,"0");
	job.parameters.put(DATA_SET_TYPE, DetrendingType.POLY_DTR.name());
	job.parameters.put(DATA_SET_TYPE_NAME,DetrendingType.POLY_DTR.longName);
	job.parameters.put(DATA_SET_ID,expId+"_"+DetrendingType.POLY_DTR.name());


	String summary = DetrendingType.POLY_DTR.longName+" min-max";
	job.parameters.put(PARAMS_SUMMARY,summary);
        
        return job;
    }    

    public static SimpleProvenance makeSimpleProvenance() {
        
        SimpleProvenance prov = new SimpleProvenance();
        prov.created = LocalDateTime.now().minus(5, ChronoUnit.DAYS);
        prov.createdBy = "creator";
        prov.modified = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        prov.modifiedBy = "modifier";
        
        return prov;
    }

    public static SecuritySummary makeSecuritySummary() {
        SecuritySummary sec = new SecuritySummary();
        sec.canRead = true;
        sec.canWrite = true;
        sec.isSuperOwner = false;
        sec.isOwner = true;
        return sec;
    }

    public static BiologicalDescription makeBiologicalDesription() {
        BiologicalDescription desc = new BiologicalDescription();
        desc.bios.add(makeBiologicalInfo("WT"));
        desc.bios.add(makeBiologicalInfo("toc1"));
        return desc;
    }

    public static BiologicalInfo makeBiologicalInfo(String genotype) {
        BiologicalInfoBuilder bio = new BiologicalInfoBuilder();
        bio.id = ids.incrementAndGet();
        bio.dataCategory = DataCategory.GEN_IMAGING;
        bio.genotype = genotype;
        bio.species = "Arabidopsis thaliana";
        return bio.build();
    }
    
}
