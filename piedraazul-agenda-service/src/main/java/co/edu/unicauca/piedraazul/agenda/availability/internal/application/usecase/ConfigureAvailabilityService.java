package co.edu.unicauca.piedraazul.agenda.availability.internal.application.usecase;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.in.ConfigureAvailabilityUseCase;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.ConfigureAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.ManageDoctorsPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.ConfigurationAvailabilityRequest;

@Service
public class ConfigureAvailabilityService implements ConfigureAvailabilityUseCase {

    private final ManageDoctorsPort manageDoctorsPort;
    private final ConfigureAvailabilityPort configureAvailabilityPort;

    public ConfigureAvailabilityService(ManageDoctorsPort manageDoctorsPort,
                                            ConfigureAvailabilityPort configureAvailabilityPort) {
        this.manageDoctorsPort = manageDoctorsPort;
        this.configureAvailabilityPort = configureAvailabilityPort;
    }

    @Override
    public DoctorAvailability configure(ConfigurationAvailabilityRequest request) {
        validateRequest(request);

        Doctor doctor = findDoctor(request.getDoctorId());

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setIntervalMinutes(request.getIntervalMinutes());
        availability.setWeekWindow(request.getWeekWindow());
        availability.setActive(true);

        return configureAvailabilityPort.save(availability);
    }

    @Override
    public DoctorAvailability update(Long availabilityId, ConfigurationAvailabilityRequest request) {
        if (availabilityId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id de la disponibilidad es obligatorio.");
        }

        validateRequest(request);

        DoctorAvailability availability = configureAvailabilityPort.findById(availabilityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una configuración de disponibilidad con id: " + availabilityId
                ));

        Doctor doctor = findDoctor(request.getDoctorId());

        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setIntervalMinutes(request.getIntervalMinutes());
        availability.setWeekWindow(request.getWeekWindow());
        availability.setActive(true);

        return configureAvailabilityPort.save(availability);
    }

    @Override
    public List<DoctorAvailability> listByDoctor(Long doctorId) {
        if (doctorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del médico es obligatorio.");
        }

        Doctor doctor = findDoctor(doctorId);

        return configureAvailabilityPort.findByDoctorAndActive(doctor);
    }

    private Doctor findDoctor(Long doctorId) {
        if (doctorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del médico es obligatorio.");
        }

        return manageDoctorsPort.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un médico/terapista con id: " + doctorId
                ));
    }

    private void validateRequest(ConfigurationAvailabilityRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud no puede estar vacía.");
        }

        if (request.getDoctorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del médico es obligatorio.");
        }

        if (request.getDayOfWeek() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El día de la semana es obligatorio.");
        }

        if (request.getStartTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio es obligatoria.");
        }

        if (request.getEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de fin es obligatoria.");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de inicio debe ser menor que la hora de fin."
            );
        }

        if (request.getIntervalMinutes() == null || request.getIntervalMinutes() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El intervalo en minutos debe ser mayor que cero."
            );
        }

        if (request.getWeekWindow() == null || request.getWeekWindow() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ventana de semanas debe ser mayor que cero."
            );
        }
    }
}
