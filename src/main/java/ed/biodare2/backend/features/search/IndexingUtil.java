/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.backend.repo.isa_dom.actors.Person;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;

/**
 *
 * @author tzielins
 */
public class IndexingUtil {
    
    public static  String authors(ContributionDesc desc) {
        StringBuilder sb = new StringBuilder();
        desc.authors.forEach( p -> sb.append(p.getName()).append(" "));
        return sb.toString();
    }

    public static String author(ContributionDesc desc) {
        if (desc.authors.isEmpty()) return "";
        Person p = desc.authors.get(0);
        return p.lastName + " " + p.firstName;
    }    
    
    public static String wholeContent(ExperimentalAssay exp) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(exp.getId()).append(" ");
        sb.append(exp.generalDesc.name).append(" ");
        sb.append(exp.generalDesc.purpose).append(" ");
        sb.append(exp.generalDesc.description).append(" ");
        sb.append(exp.generalDesc.comments).append(" ");
        
        exp.contributionDesc.authors.forEach( p -> sb.append(p.getName()).append(" "));
        exp.contributionDesc.curators.forEach( p -> sb.append(p.getName()).append(" "));
        exp.contributionDesc.institutions.forEach( p -> sb.append(p.longName).append(" "));
        exp.contributionDesc.fundings.forEach( p -> sb.append(p.institution.longName).append(" "));
        exp.contributionDesc.fundings.forEach( p -> sb.append(p.grantNr).append(" "));
        
        sb.append(exp.experimentalDetails.executionDate).append(" ");

        sb.append(exp.experimentalDetails.measurementDesc.technique).append(" ");
        sb.append(exp.experimentalDetails.measurementDesc.equipment).append(" ");
        exp.experimentalDetails.measurementDesc.parameters.parameters.values().forEach( v ->
                       sb.append(v.name).append(":").append(v.value).append(" "));
        sb.append(exp.experimentalDetails.measurementDesc.description).append(" ");

        sb.append(exp.dataCategory.longName).append(" ");
        sb.append(exp.species).append(" ");

        sb.append(exp.provenance.created).append(" ");
        sb.append(exp.provenance.modified).append(" ");

        return sb.toString();
    }  
    
    public static String trim(String txt, int max) {
        if (txt == null) return "";
        if (txt.length() <= max) return txt;
        return txt.substring(0, max);
    }    
}
