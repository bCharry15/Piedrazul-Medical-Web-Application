package co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.in.web;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.in.ConfigureAvailabilityUseCase;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.ConfigurationAvailabilityRequest;

@RestController
@RequestMapping({"/api/availability-configurations", "/api/configurations-availability"})
public class ConfigurationAvailabilityRestController {

    private final ConfigureAvailabilityUseCase configureAvailabilityUseCase;

    public ConfigurationAvailabilityRestController(ConfigureAvailabilityUseCase configureAvailabilityUseCase) {
        this.configureAvailabilityUseCase = configureAvailabilityUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorAvailability configure(@RequestBody ConfigurationAvailabilityRequest request) {
        return configureAvailabilityUseCase.configure(request);
    }

    @GetMapping("/doctor/{doctorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DoctorAvailability> listByDoctor(@PathVariable Long doctorId) {
        return configureAvailabilityUseCase.listByDoctor(doctorId);
    }

    @PutMapping("/{availabilityId}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorAvailability update(
            @PathVariable Long availabilityId,
            @RequestBody ConfigurationAvailabilityRequest request
    ) {
        return configureAvailabilityUseCase.update(availabilityId, request);
    }
}
