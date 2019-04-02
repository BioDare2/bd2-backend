/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;

/**
 *
 * @author tzielins
 */
public class MockExperimentPack implements AssayPack {
    long id;

    public MockExperimentPack(long id) {
        this.id = id;
    }
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public SystemInfo getSystemInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DBSystemInfo getDbSystemInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ExperimentalAssay getAssay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAssay(ExperimentalAssay exp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
