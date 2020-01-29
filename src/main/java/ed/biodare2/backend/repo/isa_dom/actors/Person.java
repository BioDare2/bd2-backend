/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author tzielins
 */
public class Person implements Serializable {
    
    private static final long serialVersionUID = 3L; 

    public Long id;
    public String login;
    
    @NotNull
    @NotBlank
    public String firstName;
    @NotNull
    @NotBlank
    public String lastName;
    

    public String ORCID;
    
    @JsonIgnore
    public String getName() {
        return firstName+" "+lastName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.id);
        hash = 71 * hash + Objects.hashCode(this.login);
        hash = 71 * hash + Objects.hashCode(this.lastName);
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
        final Person other = (Person) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.ORCID, other.ORCID)) {
            return false;
        }
        return true;
    }

    public static String joinNames(List<Person> persons) {
        
        return persons.stream().map( p -> p.getName()).collect(Collectors.joining(", "));
    }






    
    
}
