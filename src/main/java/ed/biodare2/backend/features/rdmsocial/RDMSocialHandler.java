/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.features.rdmsocial.dao.RDMAssetsAspectRep;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class RDMSocialHandler {
    
    final int MEASUREMENT_WARNINGS_LIMIT = 3;
    
    final Random random = new Random();
    final RDMCohort[] cohorts = RDMCohort.values();
    final RDMAssetsAspectRep assetsAspects;
    
    @Autowired
    public RDMSocialHandler(RDMAssetsAspectRep assetsAspects) {
        
        this.assetsAspects = assetsAspects;
    }
    
    
    @Transactional
    public void createUserAspect(UserAccount user) {
        
        if (user.getRdmAspect() != null) throw new IllegalArgumentException("User: "+user.getLogin()+" already has RDM aspect");
        
        RDMUserAspect aspect = new RDMUserAspect();
        aspect.setCohort(drawUserCohort());
        user.setRdmAspect(aspect);
        
    }
    
    @Transactional
    public RDMAssetsAspect registerNewAssay(AssayPack boundle, BioDare2User user) {
        return registerNewAssay(boundle, drawAssetCohort(user.getRdmAspect().cohort));
    }
    
    @Transactional
    public RDMAssetsAspect registerNewAssay(AssayPack boundle, RDMCohort cohort) {
        RDMAssetsAspect aspect = new RDMAssetsAspect();
        aspect.parentId = boundle.getId();
        aspect.entityType = EntityType.EXP_ASSAY;
        aspect.cohort = cohort;        
        aspect.measurementAdded = includesMeasurment(boundle.getAssay());
        
        return assetsAspects.save(aspect);
        
    }    
    
    
    @Transactional
    public void registerUpdateAssay(AssayPack boundle, BioDare2User user) {
        RDMAssetsAspect aspect = assetsAspects.findByParent(boundle.getId(), EntityType.EXP_ASSAY)
                .orElseThrow(()-> new IllegalStateException("Missing aspect entry for assay: "+boundle.getId()));

        aspect.updates++;
        if (!aspect.measurementAdded && includesMeasurment(boundle.getAssay())) {
            aspect.measurementAdded = true;
            aspect.measurementAddedAtWarning = aspect.measurementWarnings;
            aspect.measurementAddedAtUpdate = aspect.updates;
        }
        assetsAspects.save(aspect);
    }
    
    @Transactional
    public void registerMeasurementWarning(AssayPack boundle, BioDare2User user) {
        RDMAssetsAspect aspect = assetsAspects.findByParent(boundle.getId(), EntityType.EXP_ASSAY)
                .orElseThrow(()-> new IllegalStateException("Missing aspect entry for assay: "+boundle.getId()));

        aspect.measurementWarnings++;
        assetsAspects.save(aspect);
    }    
    
    public boolean shouldShowMeasurementWarning(AssayPack boundle, BioDare2User user) {
        if (includesMeasurment(boundle.getAssay())) return false;
        if (!isOwner(user,boundle)) return false;
        
        RDMAssetsAspect aspect = getAssayRDMAspect(boundle);
        
        if (aspect.cohort.equals(RDMCohort.CONTROL)) return false;
        if (aspect.measurementWarnings < MEASUREMENT_WARNINGS_LIMIT) return true;
        return false;
    }
    
    public boolean canIgnoreMeasurement(AssayPack boundle, BioDare2User user) {
        if (includesMeasurment(boundle.getAssay())) return true;
        if (!isOwner(user,boundle)) return true;
        
        RDMAssetsAspect aspect = getAssayRDMAspect(boundle);
        
        if (aspect.cohort.equals(RDMCohort.CONTROL) || aspect.cohort.equals(RDMCohort.ADIVSE)) return true;
        if (aspect.measurementWarnings < MEASUREMENT_WARNINGS_LIMIT) return true;
        
        return false;
    }
    
    public RDMAssetsAspect getAssayRDMAspect(AssayPack boundle) {
        RDMAssetsAspect aspect = assetsAspects.findByParent(boundle.getId(), EntityType.EXP_ASSAY)
                .orElseThrow(()-> new IllegalStateException("Missing aspect entry for assay: "+boundle.getId()));
        return aspect;
    }
    
    public RDMAssayGUIAspects getAssayGuiAspects(AssayPack boundle, BioDare2User user) {
        RDMAssayGUIAspects aspects = new RDMAssayGUIAspects();
        aspects.canProceedByMeasurement = canIgnoreMeasurement(boundle, user);
        aspects.showMeasurementWarning = shouldShowMeasurementWarning(boundle, user);
        
        return aspects;
    }
    
    protected boolean includesMeasurment(ExperimentalAssay assay) {
        if (assay.experimentalDetails.measurementDesc == null) return false;
        if (assay.experimentalDetails.measurementDesc.technique == null || assay.experimentalDetails.measurementDesc.technique.isEmpty()) return false;
        return true;
    }    

    protected RDMCohort drawUserCohort() {
        double val = random.nextDouble();
        if (val < 0.2) return RDMCohort.CONTROL;
        if (val < 0.6) return RDMCohort.ADIVSE;
        return RDMCohort.STRICT;
        //return cohorts[random.nextInt(cohorts.length)];
    }



    protected RDMCohort drawAssetCohort(RDMCohort userCohort) {
        if (userCohort.equals(RDMCohort.CONTROL)) return RDMCohort.CONTROL;
        
        if (random.nextBoolean()) return RDMCohort.CONTROL;
        return userCohort;
    }

    protected boolean isOwner(BioDare2User user, AssayPack boundle) {
        return boundle.getACL().getOwner().equals(user);
    }
}
