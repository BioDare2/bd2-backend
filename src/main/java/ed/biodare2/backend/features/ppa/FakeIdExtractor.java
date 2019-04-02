/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tzielins
 */
public class FakeIdExtractor {
    long fakeId = 1;
    Map<String, Long> labelsIds = new HashMap<>();
    Map<Long, String> idsLabels = new HashMap<>();
    Map<Long,String> dataRefs = new HashMap<>();

    public FakeIdExtractor(Collection<DataTrace> traces) {
        
        traces.forEach( t -> add(t));
    }
    
    public String getDataRef(long dataId) {
        return dataRefs.get(dataId);
    }
    
    public Collection<Long> knownIds() {
        return  idsLabels.keySet();
    }
    
    public String getBioLabel(long id) {
        return getLabel(id);
    }
    
    public String getCondLabel(long id) {
        return getLabel(id);
    }
    
    public String getLabel(long id) {
        String label = idsLabels.get(id);
        if (label == null) throw new IllegalArgumentException("Unknown id: "+id);
        return label;
    }
    
    public long getBioId(DataTrace data) {
        return getId(data);
    }

    public long getCondId(DataTrace data) {
        return getId(data);
    }

    private long getId(DataTrace data) {
        /*if (!labelsIds.containsKey(data.details.dataLabel)) {
            labelsIds.put(data.details.dataLabel, fakeId++);
            idsLabels.put(labelsIds.get(data.details.dataLabel),data.details.dataLabel);
        }*/
        if (!labelsIds.containsKey(data.details.dataLabel))
            throw new IllegalArgumentException("Unrecognized data: "+data.dataId+":"+data.details.dataLabel);
        
        return labelsIds.get(data.details.dataLabel);
    }
    
    protected void add(DataTrace data) {
        if (!labelsIds.containsKey(data.details.dataLabel)) {
            long id = fakeId++;
            labelsIds.put(data.details.dataLabel, id);
            idsLabels.put(id,data.details.dataLabel);
        }
        {
            String dataRef = data.traceNr + (data.traceRef != null ? ". ["+data.traceRef+"]": "");
            dataRefs.put((long)data.dataId, dataRef);
        }
        //return labelsIds.get(data.details.dataLabel);
    }    


    
}
