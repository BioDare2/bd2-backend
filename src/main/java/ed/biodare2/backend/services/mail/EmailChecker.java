/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.services.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class EmailChecker {

    final Logger log = LoggerFactory.getLogger(this.getClass());    

    //final Pattern usED = Pattern.compile(".+\\.edu");
    final Pattern cntED = Pattern.compile(".+\\.edu\\.[a-z]{2}");
    final Pattern acED = Pattern.compile(".+\\.ac\\.[a-z]{2}");
    final Pattern cntMed = Pattern.compile(".+\\.med\\.[a-z]{2}");
    
    final List<String> institutionDomains = new ArrayList<>(Arrays.asList(
            ".edu",
            "@mbg.csic.es",
            "@cid.csic.es",
            ".ethz.ch",
            "@ethz.ch",
            ".mpg.de",
            "@cragenomica.es",
            "@unige.ch",
            "@univ-amu.fr",
            "@upmc.fr",
            "@uc.cl",
            "@hubrecht.eu",
            ".uni-wuerzburg.de",
            "@pq.cnpq.br",
            "@cchmc.org",
            "@biologie.ens.fr",
            "@leloir.org.ar",
            "@pq.cnpq.br",
            "@cchmc.org",
            "@biologie.ens.fr",
            "@leloir.org.ar",
            "@uib.no",
            "@biol.uni.lodz.pl",
            "@u-picardie.fr",
            "@dac.unicamp.br",
            "@tuks.co.za",
            "@univ-tlse3.fr",
            "@iq.usp.br",
            "@caas.cn",
            "@upm.es",
            "@upct.es",
            "@unil.ch",
            "@med.lmu.de",
            "@agro.uba.ar",
            "@med.uni-muenchen.de",
            "@lmu.de",
            "@med.lmu.de",
            "@agro.uba.ar",
            "@med.uni-muenchen.de",
            "@lmu.de",
            "@oulu.fi",
            "@student.umu.se",
            "@rug.nl",
            "@uoguelph.ca",
            "@pl.hanze.nl",
            "@volcani.agri.gov.il",
            "@inserm.fr",
            "@pharma.uzh.ch",
            "@med.uni-muenchen.de",
            "@charite.de",
            ".uni-halle.de",
            ".concordia.ca",
            "@uv.es",
            ".uc3m.es",
            "@nudz.cz",
            "@ibpc.fr",
            "@pvcf.udl.cat",
            "@uni-konstanz.de"
            
            
    ));
    
    final Path configFile;
    
    protected EmailChecker() {
        this("academic_domains.txt");
    }
    
    @Autowired
    public EmailChecker(@Value("${bd2.academicdomains.file:academic_domains.txt}") String configPath) {
        this.configFile = Paths.get(configPath);
        
    }

    @Scheduled(fixedRate = 1000*60*10, initialDelay = 1000*60)    
    public void updateKnownDomains() {
        this.updateKnownDomains(configFile);
    }
    
    public boolean isAcademic(String email) {
        //if (usED.matcher(email).matches()) return true;
        
        if (cntED.matcher(email).matches()) return true;
        
        if (acED.matcher(email).matches()) return true;
        
        if (cntMed.matcher(email).matches()) return true;
        
        if (email.equals("zajawka@o2.pl")) return true; //the test email
        
        return hasKnownInstitutionSuffix(email);
    }
    
    protected boolean hasKnownInstitutionSuffix(String domain) {
        for (String suffix : institutionDomains) {
            if (domain.endsWith(suffix)) return true;
        }
        
        return false;
    }
    
    protected List<String> readKnownDomains(Path file) {
        if (!Files.isRegularFile(file)) {
            log.warn("Email checker cannot read domains, missing configuratin file: {}",file.toAbsolutePath());
            return Collections.emptyList();
        }
        
        try (Stream<String> lines = Files.lines(file)) {
            
            return lines
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Email checker cannot read domains, from file: {}; {}",file,e.getMessage(),e);
            return Collections.emptyList();
        }
    }
    
    protected void updateKnownDomains(Path file) {
        log.info("Updating academic domains from: {}",file);
        
        readKnownDomains(file).stream()
                .filter( d -> !institutionDomains.contains(d))
                .peek( d -> log.debug("Added new academic domain {}",d))
                .forEach( institutionDomains::add);
        
        
    }
    
    
}
