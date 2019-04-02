/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest.onto;

import ed.biodare2.backend.features.onto.species.SpeciesService;
import ed.biodare2.backend.web.rest.BioDare2Rest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/onto")
public class OntologyController extends BioDare2Rest{
    
    final SpeciesService species;
    
    @Autowired
    public OntologyController(SpeciesService species) {
        this.species = species;
    }
    
    @RequestMapping(value = "species", method = RequestMethod.GET)
    public List<String> getSpecies() {
        
        return species.findAll();
    }
    
}
