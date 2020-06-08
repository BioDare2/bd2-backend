/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.handlers.UploadFileInfo;
import ed.biodare2.backend.features.tsdata.dataimport.RegionBasedExcelTableImporterTest;
import ed.biodare2.backend.features.tsdata.dataimport.DataTableImporterTest;
import ed.biodare2.backend.features.tsdata.dataimport.TopCountImporterTest;

import ed.biodare2.backend.handlers.ExperimentDataHandlerTest;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.FileImportRequest;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeSeriesMetrics;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import ed.biodare2.backend.repo.ui_dom.tsdata.TraceSet;
import ed.robust.dom.data.DetrendingType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
@DirtiesContext
public class ExperimentDataControllerTest extends ExperimentBaseIntTest {
 
    //@Autowired
    //CacheManager cache;
    
    final String serviceRoot = "/api/experiment";
 
    protected FileImportRequest prepareImportRequest() {
      
        UploadFileInfo excelUpload = uploads.save(RegionBasedExcelTableImporterTest.getExcelTestFile(), currentUser);
        
        FileImportRequest importRequest = new FileImportRequest();
        importRequest.fileId = excelUpload.id;
        importRequest.importFormat = ImportFormat.EXCEL_TABLE;
        
        ExcelTSImportParameters param = new ExcelTSImportParameters();
    
            TimeColumnProperties time = new TimeColumnProperties();
            time.firstRow = 2;
            time.timeOffset = 0;
            time.timeType = TimeType.TIME_IN_HOURS;
            CellRangeDescription timeCol = new CellRangeDescription();
            timeCol.details = time;
            timeCol.role = CellRole.TIME;
            timeCol.range = new CellRange();
            timeCol.range.first = new CellCoordinates(1, 2);
            timeCol.range.last = new CellCoordinates(1, 2);
            
            param.timeColumn = timeCol;
            param.dataBlocks.add(timeCol);
            
            CellRangeDescription bck = new CellRangeDescription();
            bck.role = CellRole.BACKGROUND;
            bck.range = new CellRange();
            bck.range.first = new CellCoordinates(2,0);
            bck.range.last = new CellCoordinates(4,0);            
            param.dataBlocks.add(bck);
            
            CellRangeDescription data = new CellRangeDescription();
            data.role = CellRole.DATA;
            data.range = new CellRange();
            data.range.first = new CellCoordinates(5,0);
            data.range.last = new CellCoordinates(6,0);
            data.details = new DataColumnProperties("WT");
            param.dataBlocks.add(data);            

            data = new CellRangeDescription();
            data.role = CellRole.DATA;
            data.range = new CellRange();
            data.range.first = new CellCoordinates(7,0);
            data.range.last = new CellCoordinates(8,0);
            data.details = new DataColumnProperties("TOC 1");
            param.dataBlocks.add(data);            
            
        importRequest.importParameters = param;

        return importRequest;
    }
    
    protected FileImportRequest prepareTopcountImportRequest() {
      
        UploadFileInfo excelUpload = uploads.save(TopCountImporterTest.getTopCountTestFile(), currentUser);
        
        FileImportRequest importRequest = new FileImportRequest();
        importRequest.fileId = excelUpload.id;
        importRequest.importFormat = ImportFormat.TOPCOUNT;
        
        ExcelTSImportParameters param = new ExcelTSImportParameters();
    
            TimeColumnProperties time = new TimeColumnProperties();
            //time.firstRow = 2;
            time.timeOffset = 0;
            time.timeType = TimeType.TIME_IN_HOURS;
            CellRangeDescription timeCol = new CellRangeDescription();
            timeCol.details = time;
            timeCol.role = CellRole.TIME;
            //timeCol.range = new CellRange();
            //timeCol.range.first = new CellCoordinates(1, 2);
            //timeCol.range.last = new CellCoordinates(1, 2);
            
            param.timeColumn = timeCol;
            param.dataBlocks.add(timeCol);
            
            CellRangeDescription bck = new CellRangeDescription();
            bck.role = CellRole.BACKGROUND;
            bck.range = new CellRange();
            bck.range.first = new CellCoordinates(1,0);
            bck.range.last = new CellCoordinates(4,0);            
            param.dataBlocks.add(bck);
            
            CellRangeDescription data = new CellRangeDescription();
            data.role = CellRole.DATA;
            data.range = new CellRange();
            data.range.first = new CellCoordinates(5,0);
            data.range.last = new CellCoordinates(50,0);
            data.details = new DataColumnProperties("WT");
            param.dataBlocks.add(data);            

            data = new CellRangeDescription();
            data.role = CellRole.DATA;
            data.range = new CellRange();
            data.range.first = new CellCoordinates(51,0);
            data.range.last = new CellCoordinates(96,0);
            data.details = new DataColumnProperties("TOC 1");
            param.dataBlocks.add(data);            
            
        importRequest.importParameters = param;

        return importRequest;
    }
    
    
    @Test
    public void importTimeSeriesImportsDataAndReturnsDataTracesNumber() throws Exception {
    
        //ExperimentalAssay desc = DomRepoTestBuilder.makeExperiment(15);
        //exp = experiments.save(desc);
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        FileImportRequest impReq = prepareImportRequest();
                
        String orgJSON = mapper.writeValueAsString(impReq);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/ts-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importTimeSeries JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(4,info.get("imported"));
        
        
    }
    
    @Test
    public void importTimeSeriesImportsAngularRequest() throws Exception {
    
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        FileImportRequest impReq = prepareImportRequest();
        
        String orgJSON = "{\"fileId\":\""+impReq.fileId+"\"";
        orgJSON+=",\"importFormat\":\"EXCEL_TABLE\",\n" +
"\"importParameters\":{\"_class_name\":\".ExcelTSImportParameters\",\"timeColumn\":{\"range\":{\"first\":{\"col\":1,\"row\":1},\"last\":{\"col\":1,\"row\":1}},\"role\":\"TIME\",\"details\":{\"firstRow\":2,\"timeType\":\"TIME_IN_HOURS\"}},\"dataBlocks\":[{\"range\":{\"first\":{\"col\":1,\"row\":1},\"last\":{\"col\":1,\"row\":1}},\"role\":\"TIME\",\"details\":{\"firstRow\":2,\"timeType\":\"TIME_IN_HOURS\"}},{\"range\":{\"first\":{\"col\":2,\"row\":1},\"last\":{\"col\":3,\"row\":1}},\"role\":\"IGNORED\",\"details\":{}},{\"range\":{\"first\":{\"col\":4,\"row\":1},\"last\":{\"col\":5,\"row\":1}},\"role\":\"BACKGROUND\",\"details\":{}},{\"range\":{\"first\":{\"col\":6,\"row\":1},\"last\":{\"col\":9,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"WT\"}},{\"range\":{\"first\":{\"col\":10,\"row\":1},\"last\":{\"col\":13,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"toc1\"}},{\"range\":{\"first\":{\"col\":14,\"row\":1},\"last\":{\"col\":17,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"toc2\"}},{\"range\":{\"first\":{\"col\":18,\"row\":1},\"last\":{\"col\":21,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"toc3\"}},{\"range\":{\"first\":{\"col\":22,\"row\":1},\"last\":{\"col\":25,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"cca1\"}}]}}";
                
        //System.out.println("AngularIMPORT: \n"+orgJSON+"\n");
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/ts-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importTimeSeries JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(20,info.get("imported"));
        
        
    }
    
    @Test
    public void importTimeSeriesImportsTopCountDataAndReturnsDataTracesNumber() throws Exception {
    
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        FileImportRequest impReq = prepareTopcountImportRequest();
                
        String orgJSON = mapper.writeValueAsString(impReq);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/ts-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importTimeSeries JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(92,info.get("imported"));
        
        
    }
    
    @Test
    public void importsCSVDataInRowsAndReturnsDataTracesNumber() throws Exception {
    
        UploadFileInfo csvUpload = uploads.save(DataTableImporterTest.getTestDataFile("data_in_rows.csv"), currentUser);
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        FileImportRequest importRequest = new FileImportRequest();
        importRequest.fileId = csvUpload.id;
        importRequest.importFormat = ImportFormat.COMA_SEP;                
        importRequest.importParameters = DataTableImporterTest.getCSVTableInRowsParameters("data_in_rows.csv");
        
        String orgJSON = mapper.writeValueAsString(importRequest);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/ts-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importTimeSeries JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(64,info.get("imported"));
        
        
    }
    
    @Test
    public void importsCSVDataInRowsWithManualLabelsAndReturnsDataTracesNumber() throws Exception {
    
        UploadFileInfo csvUpload = uploads.save(DataTableImporterTest.getTestDataFile("data_in_rows.csv"), currentUser);
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        /*FileImportRequest importRequest = new FileImportRequest();
        importRequest.fileId = csvUpload.id;
        importRequest.importFormat = ImportFormat.COMA_SEP;                
        importRequest.importParameters = DataTableImporterTest.getCSVTableInRowsParameters("data_in_rows.csv");
        */
        
        String orgJSON = "{\"fileId\":\""+
                csvUpload.id+"\",\"importFormat\":\"COMA_SEP\",\"importParameters\":{\"inRows\":true,\"timeOffset\":0,\"importLabels\":false,\"userLabels\":[null,\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\"],\"_class_name\":\".DataTableImportParameters\",\"timeType\":\"TIME_IN_HOURS\",\"fileId\":\"_upload15398423414192388778\",\"fileName\":\"wt_prr_simpl_inRows.csv\",\"importFormat\":\"COMA_SEP\",\"firstTimeCell\":{\"col\":1,\"row\":0}}}";
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/ts-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importTimeSeries JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(48,info.get("imported"));
        
        
    }

    @Test
    public void importsExcelDataInRowsWithManualLabelsWithNewAPIAndReturnsDataTracesNumber() throws Exception {
    
        UploadFileInfo excelUpload = uploads.save(DataTableImporterTest.getTestDataFile("data_in_rows.xlsx"), currentUser);
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        /*FileImportRequest importRequest = new FileImportRequest();
        importRequest.fileId = csvUpload.id;
        importRequest.importFormat = ImportFormat.COMA_SEP;                
        importRequest.importParameters = DataTableImporterTest.getCSVTableInRowsParameters("data_in_rows.csv");
        */
        
        String orgJSON = "{\"fileId\":\""+
                excelUpload.id+"\",\"importFormat\":\"EXCEL_TABLE\",\"importParameters\":{\"inRows\":true,\"timeOffset\":0,\"importLabels\":false,\"userLabels\":[null,\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"WT LHY\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr79\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr9\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"prr79 prr5\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"WT prr7\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\",\"prr79 TOC1\"],\"_class_name\":\".DataTableImportParameters\",\"timeType\":\"TIME_IN_HOURS\",\"fileId\":\"_upload15398423414192388778\",\"fileName\":\"wt_prr_simpl_inRows.csv\",\"importFormat\":\"EXCEL_TABLE\",\"firstTimeCell\":{\"col\":1,\"row\":0}}}";
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/ts-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importTimeSeries JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(48,info.get("imported"));
        
        
    }
    
    String bd1requestJSON() throws IOException {
        Path req = (new ExperimentDataHandlerTest()).testFile("importdata.json").toPath();
        try (Stream<String> lines = Files.lines(req)) {
            return lines.collect(Collectors.joining("\n"));
        }
        
    }
    
    @Test    
    @Ignore
    public void importBD1ImportsDataAndReturnsDataTracesNumber() throws Exception {
    
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        

        
        Optional<List<DataTrace>> imported = tsHandler.getDataSet(pack, DetrendingType.NO_DTR);
        // System.out.println(imported.get());
        assertFalse(imported.isPresent());
        
        String orgJSON = bd1requestJSON();
        
        DataBundle forImport = mapper.readValue(orgJSON, DataBundle.class);
        assertFalse(forImport.data.isEmpty());
        forImport.data.forEach( t -> assertFalse(t.trace.isEmpty()));
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+exp.getId()+"/data/bd1-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("importBD1Data JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertEquals(2,info.get("imported"));
        
        imported = tsHandler.getDataSet(pack, DetrendingType.NO_DTR);
        assertTrue(imported.isPresent());
        
        List<DataTrace> series = imported.get();
        assertEquals(forImport.data.size(),series.size());
        series.forEach( s -> assertFalse(s.trace.isEmpty()));
        
    }    
    
    @Test
    public void getTSDataReturnsDataSet() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        TraceSet wrapper = mapper.readValue(resp.getResponse().getContentAsString(), TraceSet.class);
        assertNotNull(wrapper);
        List<Trace> data = wrapper.traces;
        assertNotNull(data);
        assertEquals(series, data.size());
        assertFalse(data.get(0).data.isEmpty());
        
        
    }
    
    @Test
    public void getTSDataReturnsFirstPageFromDataSetWithoutParams() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack, "LARGE");
        int DEF_PAGE_SIZE = 100;
        
        assertEquals(600, series);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        TraceSet wrapper = mapper.readValue(resp.getResponse().getContentAsString(), TraceSet.class);
        assertNotNull(wrapper);
        List<Trace> data = wrapper.traces;
        assertNotNull(data);
        assertEquals(DEF_PAGE_SIZE, data.size());
        assertFalse(data.get(0).data.isEmpty());
        // assertTrue(data.get(0).label.startsWith("1.["));        
        assertEquals(1, data.get(0).traceNr);        
        
    }
    
    @Test
    public void getTSDataReturnsPagedDataFromDataSet() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack, "LARGE");
        int pageIndex = 1;
        int pageSize = 50;
        
        assertEquals(600, series);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name())
                .param("pageIndex", ""+pageIndex)
                .param("pageSize", ""+pageSize)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        TraceSet wrapper = mapper.readValue(resp.getResponse().getContentAsString(), TraceSet.class);
        assertNotNull(wrapper);
        List<Trace> data = wrapper.traces;
        assertNotNull(data);
        assertEquals(pageSize, data.size());
        assertFalse(data.get(0).data.isEmpty());
        // System.out.println(data.get(0).label);
        // assertTrue(data.get(0).label.startsWith("51.["));
        assertEquals(51,data.get(0).traceNr);
        
    }    
    
    
    @Test
    public void getHourlyTSDataReturnsPagedDataFromDataSet() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack, "LARGE");
        int pageIndex = 1;
        int pageSize = 50;
        
        assertEquals(600, series);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name()+"/hourly")
                .param("pageIndex", ""+pageIndex)
                .param("pageSize", ""+pageSize)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        TraceSet wrapper = mapper.readValue(resp.getResponse().getContentAsString(), TraceSet.class);
        assertNotNull(wrapper);
        List<Trace> data = wrapper.traces;
        assertNotNull(data);
        assertEquals(pageSize, data.size());
        assertFalse(data.get(0).data.isEmpty());
        //System.out.println(data.get(0).label);
        //assertTrue(data.get(0).label.startsWith("51.["));
        assertEquals(51, data.get(0).traceNr);
        
        
    }    
    
    @Test
    public void getHourlyTSDataReturnsSortedPagedDataFromDataSet() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack, "SHORT");
        int pageIndex = 0;
        int pageSize = 25;
        
        //assertEquals(600, series);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name()+"/hourly")
                .param("pageIndex", ""+pageIndex)
                .param("pageSize", ""+pageSize)
                .param("sort", "ID")
                .param("direction", "desc")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        TraceSet wrapper = mapper.readValue(resp.getResponse().getContentAsString(), TraceSet.class);
        assertNotNull(wrapper);
        List<Trace> data = wrapper.traces;
        assertNotNull(data);
        //assertEquals(pageSize, data.size());
        assertFalse(data.get(0).data.isEmpty());
        //
        //System.out.println(data.get(0).label);
        //System.out.println(data.get(0).traceNr);
        //System.out.println(data.get(0).dataId);
        //assertTrue(data.get(0).label.startsWith("51.["));
        assertEquals(12, data.get(0).dataId);
        assertEquals(11, data.get(1).dataId);
        
        
    }    
    
    @Test
    // @Transactional(propagation = Propagation.REQUIRED)
    public void exportTSDataGivesCSVFile() throws Exception {
    
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        //String orgJSON = "{}";

        /*
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name()+"/export")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .with(mockAuthentication);
        */
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/"+detrending.name()+"/export")
                .contentType(APPLICATION_JSON_UTF8)
                .params(params)
                //.content(orgJSON)
                .with(mockAuthentication);        

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("text/comma-separated-values"))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("exportTSData: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        assertTrue(resp.getResponse().getContentLength() > 0);
        
        
    }
    
    @Test
    public void getTSDataMetricsGetsMetrics() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        int series = insertData(pack);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/data/metrics")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())                
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        TimeSeriesMetrics metrics = mapper.readValue(resp.getResponse().getContentAsString(), TimeSeriesMetrics.class);
        assertNotNull(metrics);
        assertEquals(series, metrics.series);
        
    }
    
}
