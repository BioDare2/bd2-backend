/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import static ed.biodare2.backend.repo.dao.PPAArtifactsRep.JOB_SIMPLE_SUMMARY_FILE;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import java.nio.file.Path;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Service
public class RhythmicityArtifactsRep {

    final static String JOB_DETAILS_FILE = "RHYTHMICITY_JOB_DETAILS.json";
    final ResourceGuard<Long> guard = new ResourceGuard<>(60);
    
    public void saveJobDetails(RhythmicityJobSummary job, AssayPack exp) {
        
        guard.guard(exp.getId(),()-> {
            
            //Path ppaDir = getPPADir(exp);
            saveJobDetails(job, exp.getId());
            // jobDetailsCache.put(new ExpJobKey(exp.getId(), job.jobId), Optional.of(job));
        });
    }    
    
    private void saveJobDetails(RhythmicityJobSummary job, long expId)  {
        
        try {
        
            Path jobFile = jobDetailsFile(expId, job.jobId);
            
        
            jobDetailsWriter.writeValue(jobFile.toFile(),job);
        
            } catch (IOException e) {
                throw new ServerSideException("Cannot save job: "+e.getMessage(),e);
            }
    }    
    
    protected Path jobDetailsFile(long expId, long jobId) {
        
        Path jobDir = getJobDir(expId,jobId);
        return jobDir.resolve(JOB_DETAILS_FILE);
    }    
    
}
