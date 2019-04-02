/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;

/**
 *
 * @author tzielins
 */
public interface AssayPack {
    
    public long getId();
    
    public abstract SystemInfo getSystemInfo();
    
    public abstract DBSystemInfo getDbSystemInfo();
    
    public abstract ExperimentalAssay getAssay();
    
    public default EntityACL getACL() {
        return getDbSystemInfo().getAcl();
    }

    public abstract void setAssay(ExperimentalAssay exp);
}
