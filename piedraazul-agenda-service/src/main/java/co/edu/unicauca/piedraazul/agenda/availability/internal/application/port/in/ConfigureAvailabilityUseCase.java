package co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.in;
import java.util.List;

import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.ConfigurationAvailabilityRequest;

public interface ConfigureAvailabilityUseCase {

    DoctorAvailability configure(ConfigurationAvailabilityRequest request);

    DoctorAvailability update(Long availabilityId, ConfigurationAvailabilityRequest request);

    List<DoctorAvailability> listByDoctor(Long doctorId);
}
