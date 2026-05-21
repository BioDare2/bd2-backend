package ed.biodare2.backend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class MultipartLoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(MultipartLoggingConfig.class);

    @Bean
    public HandlerExceptionResolver multipartExceptionResolver() {
        return (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) -> {
            if (!(ex instanceof MultipartException)) return null;

            if (isLikelyClientAbort(ex)) {
                log.debug("Aborted/malformed multipart request {} {}: {}",
                        request.getMethod(), request.getRequestURI(), rootMessage(ex));
            } else {
                log.warn("Multipart parse failed {} {}: {}",
                        request.getMethod(), request.getRequestURI(), rootMessage(ex));
            }

            if (!response.isCommitted()) {
                response.setStatus(400);
            }
            return new ModelAndView();
        };
    }

    private static boolean isLikelyClientAbort(Throwable ex) {
        String msg = rootMessage(ex).toLowerCase();
        return msg.contains("stream ended unexpectedly")
                || msg.contains("broken pipe")
                || msg.contains("connection reset")
                || msg.contains("premature eof");
    }

    private static String rootMessage(Throwable ex) {
        Throwable cur = ex;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage() != null ? cur.getMessage() : ex.toString();
    }
}
