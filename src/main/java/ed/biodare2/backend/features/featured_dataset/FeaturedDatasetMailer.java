package ed.biodare2.backend.features.featured_dataset;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.services.mail.Mailer;
import ed.biodare2.backend.handlers.UsersHandler.AccountHandlingException;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.web.rest.FeaturedDatasetController;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.system_dom.EntityType;

import java.io.IOException;

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

    public FeaturedDatasetMailer(
        Mailer mailer,
        UserAccountRep users,
        FeaturedDatasetController featured,
        DBSystemInfoRep dbSystemInfoRep
    ) {

        this.mailer = mailer;
        this.users = users;
        this.featured = featured;
        this.dbSystemInfoRep = dbSystemInfoRep;
    }

    @Scheduled(cron = "0 0 9 ? * MON")
    // @Scheduled(fixedRate = 1000 * 10, initialDelay = 1000 * 10)  // every 10 seconds (for testing)
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

        BioDare2User featured_user = info.getAcl().getOwner();
        log.info("Found featured dataset owner: " + featured_user.getLogin() + " for dataset ID: " + dataset_id);

        sendFeaturedDatasetEmail(featured_user);
        log.info("Sent featured dataset email to: " + featured_user.getEmail());
    }

    @Transactional
    public BioDare2User sendFeaturedDatasetEmail(BioDare2User user) throws AccountHandlingException {
        // Needs a second input: the featured dataset info (id, title)
        
        String to = user.getEmail();
        String subject = "BioDare2 password reset";
        String body = 
                "Dear "+user.getName()+".\n"
                +"Please use the link below to reset your password:\n\n"
                ;
        body += "------------------\n"
                + "\n------------------\n"
                + "If the link does not work please copy the whole text between ----- to your browser "
                + "\n(it has to be one line no spaces so you may need to use an editor if your mail client scrambled it)"
                +"\n\n"
                + "All the best\nBioDare"
                ;
                
        if (!mailer.send(to, subject, body)) {
            throw new AccountHandlingException("Could not send rest email");
        }
        
        return user;
    }
}
