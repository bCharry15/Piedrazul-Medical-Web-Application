package co.edu.unicauca.piedraazul.agenda.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;

import co.edu.unicauca.piedraazul.agenda.configuration.adapter.in.web.HealthController;

class HealthControllerTest {
    private final HealthEndpoint endpoint = mock(HealthEndpoint.class);
    private final HealthController controller = new HealthController(endpoint);

    @Test
    void shouldReturnHealthyStatusWithoutInternalDetails() {
        when(endpoint.health()).thenReturn(Health.up().withDetail("internal", "private").build());
        var result = controller.health();
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).containsOnlyKeys("status").containsEntry("status", "UP");
    }

    @Test
    void shouldReturn503WhenDependencyIsDownWithoutExposingError() {
        when(endpoint.health()).thenReturn(Health.down().withDetail("error", "private").build());
        var result = controller.health();
        assertThat(result.getStatusCode().value()).isEqualTo(503);
        assertThat(result.getBody()).containsOnlyKeys("status").containsEntry("status", "DOWN");
    }
}
