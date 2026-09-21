package co.edu.unicauca.piedraazul.agenda.configuration.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/agenda/health")
    public String health() {
        return "PiedraAzul Agenda funcionando correctamente";
    }
}