/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import ed.robust.dom.tsprocessing.ResultsEntry;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tzielins
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class PPAJobIndResults {
    
    @XmlElement(name="ppa")
    public List<ResultsEntry> results = new ArrayList<>();
    
    public PPAJobIndResults() {};
    
    public PPAJobIndResults(List<ResultsEntry> results) {
        this.results = results;
    };
    
}
