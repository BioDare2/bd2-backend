/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeSeriesMetrics;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.tranform.data.TimeSeriesTransformer;
import ed.robust.tranform.data.TimeSeriesTrfImp;
import ed.robust.util.timeseries.TimeSeriesOperations;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
@CacheConfig(cacheNames = {"TSData"})
public class TSDataHandler {
    
    final static String DATA_DIR = "DATA2";
    
    final TimeSeriesTransformer transformer = TimeSeriesTrfImp.getInstance();
    
    final ExperimentsStorage expStorage;
    final ObjectMapper mapper;
    
    @Autowired
    public TSDataHandler(ExperimentsStorage expStorage, ObjectMapper mapper) {
        this.expStorage = expStorage;
        this.mapper = mapper;
    }
    
    @CacheEvict(allEntries=true)
    public int handleNewData(AssayPack exp,DataBundle rawData) throws DataProcessingException {
        Path dataDir = getDataStorage(exp.getId());
        
        return handleNewData(rawData, dataDir);
    }
    
    protected Path getDataStorage(long expId) {
        try {
        Path dataDir = expStorage.getExperimentDir(expId).resolve(DATA_DIR); 
        if (!Files.exists(dataDir)) Files.createDirectory(dataDir);
        return dataDir;
        } catch (IOException e) {
            throw new ServerSideException("Cannot access data storage: "+e.getMessage(),e);
        }
    }
    
    
    protected int handleNewData(DataBundle rawData,Path dataDir) throws DataProcessingException {
        
        
        List<DataTrace> standardData = standarize(rawData);
        if (standardData.isEmpty()) throw new DataProcessingException("Empty dataset after standarization");
        
        TimeSeriesMetrics metrics = calculateMetrics(standardData);
        Map<DetrendingType,List<DataTrace>> processed = processData(standardData);
        
        storeData(processed,dataDir);
        storeMetrics(metrics, dataDir);
        
        return standardData.size();
    }

    @Cacheable(key="{#exp.getId(),#detrending}",unless="#result == null")    
    public Optional<List<DataTrace>> getDataSet(AssayPack exp,DetrendingType detrending) throws ServerSideException {
        
        Path dataDir = getDataStorage(exp.getId());
        return getDataSet(detrending,dataDir);
    }    
    
    protected Optional<List<DataTrace>> getDataSet(DetrendingType detrending,Path dataDir) throws ServerSideException {
        
        Path file = dataDir.resolve(detrending.name()+".ser");
        if (!Files.exists(file)) return Optional.empty();
        
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            
            return Optional.of((List)in.readObject());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(),e);
        } catch(IOException e) {
            throw new ServerSideException("Cannot read data set: "+e.getMessage(),e);
        }
    }

    protected List<DataTrace> standarize(DataBundle rawData) {
        
        if (rawData.backgrounds.isEmpty()) return rawData.data;
        return removeNoiseBackround(rawData.data,rawData.backgrounds);
    }

    protected List<DataTrace> removeNoiseBackround(List<DataTrace> data, List<DataTrace> backgrounds) {
        
        TimeSeries background = calculateBackground(backgrounds);
        
        List<DataTrace> cleaned = data.stream().map(DataTrace::clone).collect(Collectors.toList());
        cleaned.forEach( trace -> {
            trace.trace = substract(trace.trace,background);
        });
        
        return cleaned;
    }

    protected TimeSeries calculateBackground(List<DataTrace> backgrounds) {
        
        return averageTS(backgrounds.stream().map(dt -> dt.trace).collect(Collectors.toList()));
    }

    /**
     * Removes the background from the data. If necessary the background is casted to org data times to allow the operation
     * @param data from which to remove bacground
     * @param background trace that should be subtracted from the data.
     * @return data-backgroud
     */
    protected TimeSeries substract(TimeSeries data, TimeSeries background) {
        if (data.isEmpty() || background.isEmpty()) return data;
        try {
            return TimeSeriesOperations.substract(data, background);
        } catch (IllegalArgumentException e) {
            //The times were not matching
            background = TimeSeriesOperations.castTime(background, data);
            return TimeSeriesOperations.substract(data, background);
        }
    }
    
    protected TimeSeries averageTS(List<TimeSeries> series) {
        
        if (series == null || series.isEmpty()) return new TimeSeries();
        
        return transformer.average(series);
    }


    protected Map<DetrendingType, List<DataTrace>> processData(List<DataTrace> standardData) {
        
        Map<DetrendingType, List<DataTrace>> processed = new EnumMap<>(DetrendingType.class);
        
        for (DetrendingType detrending:DetrendingType.values()) {
            
            
            processed.put(detrending,
                    standardData.parallelStream()
                        .map( t -> detrend(t,detrending))
                        .collect(Collectors.toList()));
        }
        return processed;
    }
    
    protected DataTrace detrend(DataTrace org,DetrendingType detrending) {
        
        DataTrace dtr = org.clone();
        dtr.trace = transformer.detrend(org.trace, detrending);
        return dtr;
    }
    
    protected void storeData(Map<DetrendingType, List<DataTrace>> bundles, Path dataPath)  {
        
        for (DetrendingType detrending : bundles.keySet()) {
        
            Path file = dataPath.resolve(detrending.name()+".ser");
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(bundles.get(detrending));
            } catch (IOException e) {
                throw new ServerSideException("Cannot store data: "+e.getMessage(),e);
            }
        }
    }

    protected TimeSeriesMetrics calculateMetrics(List<DataTrace> data) {
        
        return data.parallelStream()
                .map( d -> d.trace)
                .map( TimeSeriesMetrics::fromTimeSeries)
                .reduce( TimeSeriesMetrics::reduce)
                .orElse( new TimeSeriesMetrics());
    }

    protected void storeMetrics(TimeSeriesMetrics metrics, Path dataDir) {
        
        try {
            Path file = dataDir.resolve("metrics.json");
            mapper.writeValue(file.toFile(), metrics);
        } catch (IOException e) {
            throw new ServerSideException("Cannot data metrics: "+e.getMessage(),e);
        }
    }
    
    public Optional<TimeSeriesMetrics> getMetrics(AssayPack exp) throws ServerSideException {
        
        Path dataDir = getDataStorage(exp.getId());
        return getMetrics(dataDir);
    }
    
    Optional<TimeSeriesMetrics> getMetrics(Path dataDir) throws ServerSideException {
        
        try {
            Path file = dataDir.resolve("metrics.json");
            if (!Files.exists(file)) return Optional.empty();

            TimeSeriesMetrics metrics = mapper.readValue(file.toFile(), TimeSeriesMetrics.class);
            return Optional.of(metrics);
        } catch(IOException e) {
            throw new ServerSideException("Cannot read data metrics: "+e.getMessage(),e);
        }            
    }    
    
}
