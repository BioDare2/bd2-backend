package ed.biodare2.backend.web.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

public abstract class BioDare2Rest {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    protected void sendFile(Path file,
                            String fileName,
                            String contentType,
                            boolean inline,
                            HttpServletResponse response) throws ServerSideException {

        String contentDisposition = (inline ? "inline" : "attachment")
                + "; filename=\"" + fileName + "\"";

        try {
            response.setContentLengthLong(Files.size(file));
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

            OutputStream out = response.getOutputStream();
            Files.copy(file, out);
            out.flush();

        } catch (AsyncRequestNotUsableException ex) {
            log.debug("Download aborted (response not usable) for {}: {}", file, ex.getMessage());
            return;

        } catch (IllegalStateException ex) {
            if (isClientAbort(ex)) {
                log.debug("Download aborted (response state) for {}: {}", file, ex.getMessage());
                return;
            }
            throw ex;

        } catch (IOException ex) {
            if (isClientAbort(ex)) {
                log.debug("Download aborted by client for {}: {}", file, ex.getMessage());
                return;
            }
            throw new ServerSideException("Cannot send file: " + ex.getMessage(), ex);
        }
    }

    protected boolean isClientAbort(Throwable ex) {
        Throwable cur = ex;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null) {
                String m = msg.toLowerCase();
                if (m.contains("broken pipe")
                        || m.contains("connection reset")
                        || m.contains("clientabort")
                        || m.contains("stream closed")
                        || m.contains("response not usable")
                        || m.contains("committed")
                        || m.contains("premature eof")
                        || m.contains("unexpected end of stream")) {
                    return true;
                }
            }
            cur = cur.getCause();
        }
        return false;
    }
}
