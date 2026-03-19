package ed.biodare2.backend.services.upload_guard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class UserUploadGuardTest {

    private UserUploadGuard guard;

    @BeforeEach
    void setUp() {
        guard = new UserUploadGuard();
    }

    @Test
    void tryReserve_allowsFirstReservation() {
        Long userId = 1L;

        boolean reserved = guard.tryReserve(userId);

        assertTrue(reserved);
        assertTrue(guard.hasActiveUpload(userId));
        assertNotNull(guard.activeFile(userId));
    }

    @Test
    void tryReserve_rejectsSecondReservationForSameUser() {
        Long userId = 1L;

        assertTrue(guard.tryReserve(userId));
        assertFalse(guard.tryReserve(userId));
    }

    @Test
    void tryReserve_allowsDifferentUsersIndependently() {
        Long user1 = 1L;
        Long user2 = 2L;

        assertTrue(guard.tryReserve(user1));
        assertTrue(guard.tryReserve(user2));

        assertTrue(guard.hasActiveUpload(user1));
        assertTrue(guard.hasActiveUpload(user2));
    }

    @Test
    void bindFile_setsActiveFileForReservedUser() {
        Long userId = 1L;
        String fileId = "_upload123";

        assertTrue(guard.tryReserve(userId));

        guard.bindFile(userId, fileId);

        assertEquals(fileId, guard.activeFile(userId));
        assertTrue(guard.isOwnedBy(userId, fileId));
    }

    @Test
    void bindFile_doesNothingForUnreservedUser() {
        Long userId = 1L;
        String fileId = "_upload123";

        guard.bindFile(userId, fileId);

        assertFalse(guard.hasActiveUpload(userId));
        assertNull(guard.activeFile(userId));
        assertFalse(guard.isOwnedBy(userId, fileId));
    }

    @Test
    void isOwnedBy_returnsFalseForDifferentFile() {
        Long userId = 1L;

        assertTrue(guard.tryReserve(userId));
        guard.bindFile(userId, "_upload123");

        assertFalse(guard.isOwnedBy(userId, "_upload999"));
    }

    @Test
    void isOwnedBy_returnsFalseForNullFileId() {
        Long userId = 1L;

        assertTrue(guard.tryReserve(userId));
        guard.bindFile(userId, "_upload123");

        assertFalse(guard.isOwnedBy(userId, null));
    }

    @Test
    void finish_removesActiveUpload() {
        Long userId = 1L;

        assertTrue(guard.tryReserve(userId));
        guard.bindFile(userId, "_upload123");

        guard.finish(userId);

        assertFalse(guard.hasActiveUpload(userId));
        assertNull(guard.activeFile(userId));
        assertFalse(guard.isOwnedBy(userId, "_upload123"));
    }

    @Test
    void finish_allowsUserToReserveAgain() {
        Long userId = 1L;

        assertTrue(guard.tryReserve(userId));
        guard.bindFile(userId, "_upload123");

        guard.finish(userId);

        assertTrue(guard.tryReserve(userId));
        assertTrue(guard.hasActiveUpload(userId));
    }

    @Test
    void expiredReservation_isCleanedUpAndUserCanReserveAgain() throws Exception {
        Long userId = 1L;

        assertTrue(guard.tryReserve(userId));
        assertTrue(guard.hasActiveUpload(userId));

        expireUploadState(guard, userId);

        assertFalse(guard.hasActiveUpload(userId));
        assertNull(guard.activeFile(userId));
        assertTrue(guard.tryReserve(userId));
    }

    private void expireUploadState(UserUploadGuard guard, Long userId) throws Exception {
        Field activeUploadsField = UserUploadGuard.class.getDeclaredField("activeUploads");
        activeUploadsField.setAccessible(true);
        Object activeUploads = activeUploadsField.get(guard);

        Object uploadState = ((java.util.Map<?, ?>) activeUploads).get(userId);
        assertNotNull(uploadState, "Expected upload state to exist for user");

        Field startedAtField = uploadState.getClass().getDeclaredField("startedAt");
        startedAtField.setAccessible(true);

        // Make it old enough to exceed the timeout
        startedAtField.set(uploadState, java.time.Instant.now().minus(Duration.ofMinutes(31)));
    }
}
