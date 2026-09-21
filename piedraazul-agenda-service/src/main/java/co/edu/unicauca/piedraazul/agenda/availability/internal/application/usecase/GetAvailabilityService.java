package co.edu.unicauca.piedraazul.agenda.availability.internal.application.usecase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.AvailabilityResponse;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.in.GetAvailabilityUseCase;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.FindDoctorAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.service.AvailabilityStrategy;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

@Service
public class GetAvailabilityService implements GetAvailabilityUseCase {

    private final FindDoctorPort findDoctorPort;
    private final FindDoctorAvailabilityPort findDoctorAvailabilityPort;
    private final FindAppointmentsPort findAppointmentsPort;
    private final AvailabilityStrategy availabilityStrategy;

    public GetAvailabilityService(
            FindDoctorPort findDoctorPort,
            FindDoctorAvailabilityPort findDoctorAvailabilityPort,
            FindAppointmentsPort findAppointmentsPort,
            AvailabilityStrategy availabilityStrategy) {

        this.findDoctorPort = findDoctorPort;
        this.findDoctorAvailabilityPort = findDoctorAvailabilityPort;
        this.findAppointmentsPort = findAppointmentsPort;
        this.availabilityStrategy = availabilityStrategy;
    }

    @Override
    public AvailabilityResponse get(Long doctorId, LocalDate date) {

        Doctor doctor = findDoctorPort.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un médico/terapista con id: " + doctorId
                ));

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        DoctorAvailability availability = findDoctorAvailabilityPort
                .findActiveAvailability(doctor, dayOfWeek)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El médico/terapista no tiene disponibilidad configurada para el día: "
                                + dayOfWeek
                ));

        validateSchedulingWindow(
                availability,
                date
        );

        List<Appointment> dailyAppointments =
                findAppointmentsPort.findByDoctorAndDate(
                        doctor,
                        date
                );

        Set<LocalTime> occupiedTimes =
                new HashSet<>();

        for (Appointment appointment : dailyAppointments) {

            /*
             * Una cita cancelada deja de ocupar la franja.
             *
             * Las demás citas continúan bloqueando el horario porque
             * representan una reserva o una cita que efectivamente
             * utilizó esa franja.
             */
            if (appointment.getStatus()
                    != AppointmentStatus.CANCELADA) {

                occupiedTimes.add(
                        appointment.getTime()
                );
            }
        }

        List<LocalTime> availableSlots =
                availabilityStrategy
                        .calculateAvailableSlots(
                                availability.getStartTime(),
                                availability.getEndTime(),
                                availability.getIntervalMinutes(),
                                occupiedTimes
                        );

        return new AvailabilityResponse(
                doctor.getId(),
                doctor.getFullName(),
                date,
                availability.getIntervalMinutes(),
                availability.getStartTime(),
                availability.getEndTime(),
                availableSlots
        );
    }

    private void validateSchedulingWindow(
            DoctorAvailability availability,
            LocalDate date) {

        Integer weekWindow =
                availability.getWeekWindow();

        if (weekWindow == null
                || weekWindow <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ventana de agendamiento configurada no es válida."
            );
        }

        LocalDate currentDate =
                LocalDate.now();

        LocalDate limitDate =
                currentDate.plusWeeks(
                        weekWindow
                );

        if (date.isBefore(currentDate)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede obtener disponibilidad para fechas pasadas."
            );
        }

        if (date.isAfter(limitDate)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha supera la ventana de agendamiento permitida de "
                            + weekWindow
                            + " semanas."
            );
        }
    }
}