/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import ed.biodare2.backend.repo.system_dom.EntityType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Zielu
 */
public class RDMAssetsAspect {
    
    public RDMCohort cohort = RDMCohort.CONTROL;

    @NotNull
    public long parentId;
    
    @NotNull
    public EntityType entityType;  
    
    public int updates;
    
    public boolean measurementAdded = false;
    public int measurementWarnings = 0;
    public int measurementAddedAtWarning = 0;
    public int measurementAddedAtUpdate = 0;
}
