package co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.in;
import java.time.LocalDate;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.AvailabilityResponse;

public interface GetAvailabilityUseCase {

    AvailabilityResponse get(Long doctorId, LocalDate date);
}
