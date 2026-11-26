package co.edu.unicauca.piedraazul.agenda.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

class RequestLoggingFilterTest {
    @Test
    void shouldLogRouteTemplateWithoutCredentialsPatientDocumentOrQueryAndRestoreContext() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        MDC.put("requestId", "previous-context");
        try {
            var request = new MockHttpServletRequest("GET", "/api/appointments/patient/123456789");
            request.setQueryString("password=secret-value");
            request.addHeader("Authorization", "Bearer private-token");
            request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/api/appointments/patient/{documentNumber}");
            var response = new MockHttpServletResponse();
            new RequestLoggingFilter().doFilter(request, response, (req, res) -> response.setStatus(403));
            assertThat(response.getHeader("X-Request-ID")).isNotBlank();
            assertThat(MDC.get("requestId")).isEqualTo("previous-context");
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getFormattedMessage())
                    .contains("status=403", "/patient/{documentNumber}")
                    .doesNotContain("123456789", "secret-value", "private-token");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            MDC.remove("requestId");
        }
    }
}
