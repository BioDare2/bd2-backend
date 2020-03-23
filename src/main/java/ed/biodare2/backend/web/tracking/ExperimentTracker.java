/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.tracking;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.isa_dom.assets.AssetVersion;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import static ed.biodare2.backend.web.tracking.TargetType.*;
import static ed.biodare2.backend.web.tracking.ActionType.*;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.ppa.PPAMethod;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentTracker extends AbstractTracker {
    
    final Logger analysis = LoggerFactory.getLogger("analysis");

    public void experimentList(BioDare2User user) {
        track(EXPERIMENT,LIST,user);
    }
    
    public void experimentSearch(String query, BioDare2User user) {
        track(EXPERIMENT,SEARCH,query, user);
    }    

    public void experimentDraft(BioDare2User user) {
        track(EXPERIMENT,DRAFT,user);
    }
    
    public void experimentNew(long id,BioDare2User user) {
        track(EXPERIMENT,NEW,id,user);
    }

    public void experimentImport(long id,BioDare2User user) {
        track(EXPERIMENT,IMPORT,id,user);
    }    

    public void experimentView(AssayPack exp, BioDare2User user) {
        track(EXPERIMENT,VIEW,exp.getId(),user);
    }



    public void experimentUpdate(AssayPack exp, BioDare2User user) {
        track(EXPERIMENT,UPDATE,exp.getId(),user);
    }
    
    public void experimentPublish(AssayPack exp, BioDare2User user) {
        track(EXPERIMENT,PUBLISH,exp.getId(),user);
    }    


    public void dataImport(AssayPack exp, BioDare2User user) {
        track(EXP_DATA,NEW,exp.getId(),user);
    }

    public void dataView(AssayPack exp, DetrendingType detrending, BioDare2User user) {
        track(EXP_DATA,VIEW,exp.getId(),user,detrending.name());
    }

    public void dataDownload(AssayPack exp, DetrendingType detrending, BioDare2User user) {
        track(EXP_DATA,DOWNLOAD,exp.getId(),user,detrending.name());
    }

    public void fileList(AssayPack exp, BioDare2User user) {
        track(EXP_FILE,LIST,exp.getId(),user);
    }
    
    public void fileNew(AssayPack exp, FileAsset file, BioDare2User user) {
        track(EXP_FILE,NEW,exp.getId(),user,file.id);
    }
    
    public void fileDownload(AssayPack exp, long fileId,AssetVersion asset, BioDare2User user) {
        track(EXP_FILE,DOWNLOAD,exp.getId(),user,fileId+":"+asset.versionId);
    }


    public void ppaList(AssayPack exp, BioDare2User user) {
        track(EXP_PPA,LIST,exp.getId(),user);
    }

    public void ppaNew(AssayPack exp, long analysisId, PPAMethod method, BioDare2User user) {
        track(EXP_PPA,NEW,exp.getId(),user,method.name(),analysisId);
        analysis.info("{}\t{}\t{}\t{}\t{}\t{}\t",EXP_PPA,NEW,exp.getId(),method.name(),analysisId,user.getLogin(),user.getSupervisor().getLogin());
    }
    
    public void ppaNew(AssayPack exp, UUID analysisId, PPAMethod method, BioDare2User user) {
        track(EXP_PPA,NEW,exp.getId(),user,method.name(),analysisId.toString());
        analysis.info("{}\t{}\t{}\t{}\t{}\t{}\t",EXP_PPA,NEW,exp.getId(),method.name(),analysisId,user.getLogin(),user.getSupervisor().getLogin());
    }    

    public void ppaStats(AssayPack exp, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"STATS");
    }

    public void ppaResults(AssayPack exp, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"INDIVIDUALS");
    }

    public void ppaJob(AssayPack exp, JobSummary res, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB",res.getJobId());
    }
    
    public void ppaJob(AssayPack exp, PPAJobSummary res, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB",res.jobId);
    }    
    
    public void ppaDeleteJob(AssayPack exp, PPAJobSummary job, BioDare2User user) {
        track(EXP_PPA,DELETE,exp.getId(),user,"JOB",job.jobId);
    }   
    
    public void ppaDeleteJob(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,DELETE,exp.getId(),user,"JOB",jobId);
    }     
    
    public void ppaJobDownload(AssayPack exp, long jobId, BioDare2User user) {
        track(EXP_PPA,DOWNLOAD,exp.getId(),user,"JOB",jobId);
    }    
    
    public void ppaJobDownload(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,DOWNLOAD,exp.getId(),user,"JOB",jobId);
    }    

    public void ppaJobGroupedResults(AssayPack exp, long jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_GROUPED_RESULTS",jobId);
    }
    
    public void ppaJobGroupedResults(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_GROUPED_RESULTS",jobId);
    }    
    
    public void ppaJobSimpleResults(AssayPack exp, long jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_RESULTS",jobId);
    }    
    
    public void ppaJobSimpleResults(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_RESULTS",jobId);
    }    
    
    public void ppaJobStats(AssayPack exp, long jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_STATS",jobId);
    }    
    
    public void ppaJobStats(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_STATS",jobId);
    }    
    
    
    public void ppaForSelect(AssayPack exp, long jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_RESULTS",jobId);
    }
    
    public void ppaForSelect(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"JOB_RESULTS",jobId);
    }    

    public void ppaSelect(AssayPack exp, long jobId, BioDare2User user) {
        track(EXP_PPA,UPDATE,exp.getId(),user,"JOB_RESULTS",jobId);
    }

    public void ppaSelect(AssayPack exp, String jobId, BioDare2User user) {
        track(EXP_PPA,UPDATE,exp.getId(),user,"JOB_RESULTS",jobId);
    }
    
    public void ppaFit(AssayPack exp, long jobId, long dataId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"FIT",jobId,dataId);
    }
    
    public void ppaFit(AssayPack exp, String jobId, long dataId, BioDare2User user) {
        track(EXP_PPA,VIEW,exp.getId(),user,"FIT",jobId,dataId);
    }    

    public void ppaDownload(AssayPack exp, BioDare2User user) {
        track(EXP_PPA,DOWNLOAD,exp.getId(),user);
    }

    public void rhythmicityNew(AssayPack exp, String analysisId, String method, BioDare2User user) {
        track(EXP_RHYTHMICITY,NEW,exp.getId(),user,method,analysisId);
        analysis.info("{}\t{}\t{}\t{}\t{}\t{}\t",EXP_RHYTHMICITY,NEW,exp.getId(),method,analysisId,user.getLogin(),user.getSupervisor().getLogin());
    }

    public void rhythmicityJob(AssayPack exp, RhythmicityJobSummary res, BioDare2User user) {
        track(EXP_RHYTHMICITY,VIEW,exp.getId(),user,"JOB",res.jobId.toString());
    }

    public void rhythmicityResults(AssayPack exp, UUID jobId, BioDare2User user) {
        track(EXP_RHYTHMICITY,VIEW,exp.getId(),user,"JOB_RESULTS",jobId.toString());
    }

    public void rhythmicityDeleteJob(AssayPack exp, RhythmicityJobSummary job, BioDare2User user) {
        track(EXP_RHYTHMICITY,DELETE,exp.getId(),user,"JOB",job.jobId.toString());
    }  

    public void rhythmicityJobDownload(AssayPack exp, UUID jobId, BioDare2User user) {
        track(EXP_RHYTHMICITY,DOWNLOAD,exp.getId(),user,"JOB",jobId.toString());
    }    

    












}
