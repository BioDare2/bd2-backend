/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare2.backend.repo.ui_dom.shared.Page;
import java.util.Objects;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class Slice {
    
    public Page rowPage = new Page();
    public Page colPage = new Page();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.rowPage);
        hash = 67 * hash + Objects.hashCode(this.colPage);
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
        final Slice other = (Slice) obj;
        if (!Objects.equals(this.rowPage, other.rowPage)) {
            return false;
        }
        if (!Objects.equals(this.colPage, other.colPage)) {
            return false;
        }
        return true;
    }
    
    
}
