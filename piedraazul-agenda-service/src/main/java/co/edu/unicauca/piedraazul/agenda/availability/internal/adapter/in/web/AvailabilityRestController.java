package co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.in.web;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.in.GetAvailabilityUseCase;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.AvailabilityResponse;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityRestController {

    private final GetAvailabilityUseCase getAvailabilityUseCase;

    public AvailabilityRestController(GetAvailabilityUseCase getAvailabilityUseCase) {
        this.getAvailabilityUseCase = getAvailabilityUseCase;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AvailabilityResponse getAvailability(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return getAvailabilityUseCase.get(doctorId, date);
    }
}
