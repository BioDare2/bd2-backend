/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
//@Service
public class IdGenerators {
    
    final LongRecordManager recordManager;
    final Map<String,IdGenerator> generators = new ConcurrentHashMap<>();
    
    @Autowired
    public IdGenerators(LongRecordManager recordManager) {
        this.recordManager = recordManager;
    }
    
    public void initGenerator(String name,int increament,long START_VAL,long MAX_VAL) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Generator name cannot be empty");
        if (increament < 1) throw new IllegalArgumentException("Increament must be >= 1");
        
        DBIdGenerator gen = (DBIdGenerator)generators.putIfAbsent(name, DBIdGenerator.newInstance(name,increament,START_VAL,MAX_VAL,recordManager));
        if (gen != null) gen.updateStart(START_VAL);
    }
    
    public void initGenerator(String name,int increament) {
        initGenerator(name, increament,1,Long.MAX_VALUE);
    }
    
    
    public void initGenerators(Map<String,Integer> generators) {
        for (Map.Entry<String,Integer> ent : generators.entrySet())
            initGenerator(ent.getKey(), ent.getValue());
    }
    
    public IdGenerator getGenerator(String name) {
        IdGenerator gen = generators.get(name);
        if (gen == null) throw new IllegalArgumentException("Unknown generator: "+name+"; please configure it first");
        return gen;
    }
}
