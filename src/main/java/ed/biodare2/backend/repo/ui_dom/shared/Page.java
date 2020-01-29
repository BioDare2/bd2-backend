/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class Page {
    
    public int pageIndex;
    public int pageSize;
    public int length;
    
    public Page() {};

    public Page(int pageIndex, int pageSize) {
        if (pageIndex < 0) throw new IllegalArgumentException("Page index must be >= 0 "+pageIndex);
        if (pageSize < 1) throw new IllegalArgumentException("Page size must be possitive "+pageSize);
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }
    
    public Page(int pageIndex, int pageSize, int length) {
        this(pageIndex, pageSize);
        this.length = length;
    }    
    
    
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
        if (this.length != other.length) {
            return false;
        }        
        return true;
    }
    
    
}
