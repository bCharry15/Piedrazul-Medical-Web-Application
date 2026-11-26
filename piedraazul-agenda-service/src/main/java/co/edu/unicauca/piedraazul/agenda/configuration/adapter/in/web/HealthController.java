package co.edu.unicauca.piedraazul.agenda.configuration.adapter.in.web;

import java.util.Map;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/api/agenda/health")
    public ResponseEntity<Map<String, String>> health() {
        Status status = healthEndpoint.health().getStatus();
        return ResponseEntity.status(Status.UP.equals(status) ? 200 : 503)
                .body(Map.of("status", status.getCode()));
    }
}
