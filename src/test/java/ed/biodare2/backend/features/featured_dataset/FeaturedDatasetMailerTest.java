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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeaturedDatasetMailerTest {

    Mailer mailer;
    UserAccountRep users;
    FeaturedDatasetController featured;
    DBSystemInfoRep dbSystemInfoRep;
    ExperimentalAssayRep experimentalAssayRep;
    FeaturedDatasetMailer mailerService;

    @BeforeEach
    void setUp() {
        mailer = mock(Mailer.class);
        users = mock(UserAccountRep.class);
        featured = mock(FeaturedDatasetController.class);
        dbSystemInfoRep = mock(DBSystemInfoRep.class);
        experimentalAssayRep = mock(ExperimentalAssayRep.class);

        mailerService = new FeaturedDatasetMailer(
                mailer, users, featured, dbSystemInfoRep, experimentalAssayRep
        );
    }

    @Test
    void sendFeaturedDatasetEmail_sendsEmailAndReturnsUser() throws AccountHandlingException {
        BioDare2User user = mock(BioDare2User.class);
        ExperimentalAssay assay = mock(ExperimentalAssay.class);

        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getName()).thenReturn("Test User");
        when(assay.getName()).thenReturn("Test Experiment");
        when(mailer.send(anyString(), anyString(), anyString())).thenReturn(true);

        BioDare2User result = mailerService.sendFeaturedDatasetEmail(user, assay);

        assertEquals(user, result);
        verify(mailer).send(eq("test@example.com"), contains("Experiment featured"), contains("Test Experiment"));
    }

    @Test
    void sendFeaturedDatasetEmail_throwsExceptionIfMailFails() {
        BioDare2User user = mock(BioDare2User.class);
        ExperimentalAssay assay = mock(ExperimentalAssay.class);

        when(user.getEmail()).thenReturn("fail@example.com");
        when(user.getName()).thenReturn("Fail User");
        when(assay.getName()).thenReturn("Fail Experiment");
        when(mailer.send(anyString(), anyString(), anyString())).thenReturn(false);

        assertThrows(AccountHandlingException.class, () -> {
            mailerService.sendFeaturedDatasetEmail(user, assay);
        });
    }

    @Test
    void emailFeaturedDatasetAuthor_sendsEmailIfAllDataPresent() throws Exception {
        Long datasetId = 42L;
        when(featured.getFeaturedDatasetId()).thenReturn(datasetId);

        DBSystemInfo info = mock(DBSystemInfo.class);
        BioDare2User user = mock(BioDare2User.class);
        ExperimentalAssay assay = mock(ExperimentalAssay.class);

        when(dbSystemInfoRep.findByParentIdAndEntityType(datasetId, EntityType.EXP_ASSAY)).thenReturn(Optional.of(info));
        when(info.getAcl()).thenReturn(mock(ed.biodare2.backend.security.dao.db.EntityACL.class));
        when(info.getAcl().getOwner()).thenReturn(user);
        when(experimentalAssayRep.findOne(datasetId)).thenReturn(Optional.of(assay));
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getName()).thenReturn("Test User");
        when(assay.getName()).thenReturn("Test Experiment");
        when(mailer.send(anyString(), anyString(), anyString())).thenReturn(true);

        mailerService.emailFeaturedDatasetAuthor();

        verify(mailer).send(eq("test@example.com"), contains("Experiment featured"), contains("Test Experiment"));
    }

    @Test
    void emailFeaturedDatasetAuthor_returnsIfNoFeaturedId() throws Exception {
        when(featured.getFeaturedDatasetId()).thenThrow(new IOException("No ID"));

        mailerService.emailFeaturedDatasetAuthor();

        verifyNoInteractions(dbSystemInfoRep);
        verifyNoInteractions(experimentalAssayRep);
        verifyNoInteractions(mailer);
    }

    @Test
    void emailFeaturedDatasetAuthor_returnsIfNoDBSystemInfo() throws Exception {
        Long datasetId = 42L;
        when(featured.getFeaturedDatasetId()).thenReturn(datasetId);
        when(dbSystemInfoRep.findByParentIdAndEntityType(datasetId, EntityType.EXP_ASSAY)).thenReturn(Optional.empty());

        mailerService.emailFeaturedDatasetAuthor();

        verifyNoInteractions(experimentalAssayRep);
        verifyNoInteractions(mailer);
    }

    @Test
    void emailFeaturedDatasetAuthor_returnsIfNoExperimentalAssay() throws Exception {
        Long datasetId = 42L;
        DBSystemInfo info = mock(DBSystemInfo.class);

        when(featured.getFeaturedDatasetId()).thenReturn(datasetId);
        when(dbSystemInfoRep.findByParentIdAndEntityType(datasetId, EntityType.EXP_ASSAY)).thenReturn(Optional.of(info));
        when(experimentalAssayRep.findOne(datasetId)).thenReturn(Optional.empty());

        mailerService.emailFeaturedDatasetAuthor();

        verifyNoInteractions(mailer);
    }
}
