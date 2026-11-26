package co.edu.unicauca.piedraazul.agenda.configuration;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

/** Records request outcomes without bodies, credentials, queries or patient IDs. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String previousId = MDC.get("requestId");
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-ID", requestId);
        long started = System.nanoTime();
        boolean failed = false;
        try {
            chain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            failed = true;
            throw exception;
        } finally {
            Object route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            int status = failed ? 500 : response.getStatus();
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
            String pattern = route == null ? "unmatched" : route.toString();
            if (status >= 500) {
                log.error("HTTP method={} route={} status={} durationMs={}",
                        request.getMethod(), pattern, status, elapsed);
            } else if (status >= 400) {
                log.warn("HTTP method={} route={} status={} durationMs={}",
                        request.getMethod(), pattern, status, elapsed);
            } else {
                log.info("HTTP method={} route={} status={} durationMs={}",
                        request.getMethod(), pattern, status, elapsed);
            }
            if (previousId == null) {
                MDC.remove("requestId");
            } else {
                MDC.put("requestId", previousId);
            }
        }
    }
}
