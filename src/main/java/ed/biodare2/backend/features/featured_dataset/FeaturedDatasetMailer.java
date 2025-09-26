package ed.biodare2.backend.features.featured_dataset;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.services.mail.Mailer;
import ed.biodare2.backend.handlers.UsersHandler.AccountHandlingException;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.web.rest.FeaturedDatasetController;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.system_dom.EntityType;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class FeaturedDatasetMailer {

    final UserAccountRep users;
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final Mailer mailer;
    final FeaturedDatasetController featured;
    final DBSystemInfoRep dbSystemInfoRep;
    final ExperimentalAssayRep experimentalAssayRep;

    public FeaturedDatasetMailer(
        Mailer mailer,
        UserAccountRep users,
        FeaturedDatasetController featured,
        DBSystemInfoRep dbSystemInfoRep,
        ExperimentalAssayRep experimentalAssayRep
    ) {

        this.mailer = mailer;
        this.users = users;
        this.featured = featured;
        this.dbSystemInfoRep = dbSystemInfoRep;
        this.experimentalAssayRep = experimentalAssayRep;
    }

    @Scheduled(cron = "0 0 9 ? * MON")
    // @Scheduled(fixedRate = 1000 * 1000, initialDelay = 1000 * 5)  // every 5 seconds (for testing)
    @Transactional
    public void emailFeaturedDatasetAuthor() throws AccountHandlingException {
        Long dataset_id = null;
        try {
            dataset_id = featured.getFeaturedDatasetId();
        } catch (IOException e) {
            log.error("Failed to get featured dataset ID", e);
            return;
        }

        DBSystemInfo info = dbSystemInfoRep.findByParentIdAndEntityType(dataset_id, EntityType.EXP_ASSAY)
                                           .orElse(null);
        if (info == null) {
            log.warn("No DBSystemInfo found for dataset ID: " + dataset_id);
            return;
        }

        Optional<ExperimentalAssay> assayOpt = experimentalAssayRep.findOne(dataset_id);
        ExperimentalAssay assay = null;
        if (assayOpt.isPresent()) {
            assay = assayOpt.get();
        } else {
            log.warn("No ExperimentalAssay found for dataset ID: " + dataset_id);
            return;
        }

        BioDare2User featured_user = info.getAcl().getOwner();
        log.info("Found featured dataset owner: " + featured_user.getLogin() + " for dataset ID: " + dataset_id);

        sendFeaturedDatasetEmail(featured_user, assay);
        log.info("Sent featured dataset email to: " + featured_user.getEmail() + " for dataset ID: " + dataset_id);
    }

    @Transactional
    public BioDare2User sendFeaturedDatasetEmail(BioDare2User user, ExperimentalAssay assay) throws AccountHandlingException {
        
        String to = user.getEmail();
        String subject = "Experiment featured on BioDare2!";
        String body = 
                "Dear " + user.getName() + ",\n\n"
                + "Your experiment \"" + assay.getName() + "\" is being featured on the BioDare2 homepage this week!\n\n"
                + "Every week, we feature an experiment with high-quality metadata on our homepage.\n\n"
                + "Many thanks for describing your experiment on BioDare2 and contributing to open science!\n\n"
                + "All the best,\nBioDare"
                ;
                
        if (!mailer.send(to, subject, body)) {
            throw new AccountHandlingException("Could not send rest email");
        }
        
        return user;
    }
}
