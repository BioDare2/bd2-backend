/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class Page {
    
    public int pageIndex;
    public int pageSize;
    
    @JsonIgnore
    public int first() {
        return pageIndex*pageSize;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.pageIndex;
        hash = 23 * hash + this.pageSize;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Page other = (Page) obj;
        if (this.pageIndex != other.pageIndex) {
            return false;
        }
        if (this.pageSize != other.pageSize) {
            return false;
        }
        return true;
    }
    
    
}
