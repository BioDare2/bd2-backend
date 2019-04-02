/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.contribution;

import ed.biodare2.backend.repo.isa_dom.actors.Institution;
import ed.biodare2.backend.repo.isa_dom.actors.Person;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Zielu
 */
public class ContributionDesc implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public List<Person> authors = new ArrayList<>();
    public List<Person>  curators = new ArrayList<>();
    public List<Institution> institutions = new ArrayList<>();
    public List<Funding> fundings = new ArrayList<>();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.authors);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContributionDesc other = (ContributionDesc) obj;
        if (!Objects.equals(this.authors, other.authors)) {
            return false;
        }
        if (!Objects.equals(this.curators, other.curators)) {
            return false;
        }
        if (!Objects.equals(this.institutions, other.institutions)) {
            return false;
        }
        if (!Objects.equals(this.fundings, other.fundings)) {
            return false;
        }
        return true;
    }
    
    
}
