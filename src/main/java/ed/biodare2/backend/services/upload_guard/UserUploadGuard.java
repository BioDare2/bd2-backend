package ed.biodare2.backend.services.upload_guard;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserUploadGuard {

    private static final String PENDING = "__PENDING__";
    private static final Duration TIMEOUT = Duration.ofMinutes(30);

    private static class UploadState {
	final String fileId;
	final Instant startedAt;

	UploadState(String fileId, Instant startedAt) {
	    this.fileId = fileId;
	    this.startedAt = startedAt;
	}
    }

    private final Map<Long, UploadState> activeUploads = new ConcurrentHashMap<>();

    public boolean tryReserve(Long userId) {
	cleanupExpired(userId);
        return activeUploads.putIfAbsent(userId, new UploadState(PENDING, Instant.now())) == null;
    }

    public void bindFile(Long userId, String fileId) {
        activeUploads.computeIfPresent(userId, (id, current) -> new UploadState(fileId, current.startedAt));
    }

    public boolean isOwnedBy(Long userId, String fileId) {
	cleanupExpired(userId);
	UploadState state = activeUploads.get(userId);
        return state != null && fileId != null && fileId.equals(state.fileId);
    }

    public boolean hasActiveUpload(Long userId) {
	cleanupExpired(userId);
        return activeUploads.containsKey(userId);
    }

    public void finish(Long userId) {
        activeUploads.remove(userId);
    }

    public String activeFile(Long userId) {
	cleanupExpired(userId);
	UploadState state = activeUploads.get(userId);
        return state == null ? null : state.fileId;
    }

    private void cleanupExpired(long userId) {
	UploadState state = activeUploads.get(userId);
	if (state != null && state.startedAt.plus(TIMEOUT).isBefore(Instant.now())) {
	    activeUploads.remove(userId);
	}
    }
}
