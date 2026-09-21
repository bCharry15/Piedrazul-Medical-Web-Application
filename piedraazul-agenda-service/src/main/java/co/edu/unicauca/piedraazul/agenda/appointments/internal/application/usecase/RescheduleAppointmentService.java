package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.RescheduleAppointmentRequest;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.RescheduleAppointmentUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.SaveReschedulingHistoryPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;
import jakarta.transaction.Transactional;

@Service
public class RescheduleAppointmentService implements RescheduleAppointmentUseCase {

    private final FindAppointmentsPort findAppointmentsPort;
    private final SaveReschedulingHistoryPort saveReschedulingHistoryPort;

    public RescheduleAppointmentService(FindAppointmentsPort findAppointmentsPort,
                                SaveReschedulingHistoryPort saveReschedulingHistoryPort) {
        this.findAppointmentsPort = findAppointmentsPort;
        this.saveReschedulingHistoryPort = saveReschedulingHistoryPort;
    }

    @Override
    @Transactional
    public Map<String, Object> reschedule(Long appointmentId, RescheduleAppointmentRequest request) {
        validateIdAppointment(appointmentId);
        validateRequest(request);

        Appointment appointment = findAppointmentsPort.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una cita con id: " + appointmentId
                ));

        validateReschedulableAppointment(appointment);

        LocalDate previousDate = appointment.getDate();
        LocalTime previousTime = appointment.getTime();

        LocalDate newDate = request.getNewDate();
        LocalTime newTime = request.getNewTime();

        validateDateTime(previousDate, previousTime, newDate, newTime);
        validateTimeSlotAvailable(appointment, newDate, newTime);

        ReschedulingHistory history = createHistory(
                appointment,
                previousDate,
                previousTime,
                newDate,
                newTime,
                request
        );

        saveReschedulingHistoryPort.save(history);

        appointment.setDate(newDate);
        appointment.setTime(newTime);
        appointment.setNotes(buildReschedulingNotes(appointment, history));

        Appointment updatedAppointment = findAppointmentsPort.save(appointment);

        Map<String, Object> response = convertAppointmentToMap(updatedAppointment);
        response.put("mensaje", "Cita re-agendada correctamente.");
        response.put("dateAnterior", previousDate);
        response.put("timeAnterior", previousTime);
        response.put("dateNueva", newDate);
        response.put("timeNueva", newTime);

        return response;
    }

    private void validateIdAppointment(Long appointmentId) {
        if (appointmentId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id de la cita es obligatorio."
            );
        }
    }

    private void validateRequest(RescheduleAppointmentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud de re-agendamiento no puede estar vacía."
            );
        }

        if (request.getNewDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva fecha de la cita es obligatoria."
            );
        }

        if (request.getNewTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva hora de la cita es obligatoria."
            );
        }
    }

    private void validateReschedulableAppointment(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.ATENDIDA
                || appointment.getStatus() == AppointmentStatus.COMPLETADA
                || appointment.getStatus() == AppointmentStatus.CANCELADA
                || appointment.getStatus() == AppointmentStatus.NO_VINO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cita ya está en un estado final y no puede ser re-agendada."
            );
        }
    }

    private void validateDateTime(LocalDate previousDate,
                                  LocalTime previousTime,
                                  LocalDate newDate,
                                  LocalTime newTime) {
        if (newDate.equals(previousDate) && newTime.equals(previousTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva fecha y hora son iguales a la fecha y hora actual de la cita."
            );
        }

        if (newDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede re-agendar una cita para una fecha pasada."
            );
        }
    }

    private void validateTimeSlotAvailable(Appointment appointment, LocalDate date, LocalTime time) {
        boolean timeSlotOccupied = findAppointmentsPort.isTimeSlotOccupiedForAnotherAppointment(
                appointment.getDoctor(),
                date,
                time,
                appointment.getId()
        );

        if (timeSlotOccupied) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El médico ya tiene una cita registrada en la nueva fecha y hora seleccionada."
            );
        }
    }

    private ReschedulingHistory createHistory(Appointment appointment,
                                                   LocalDate previousDate,
                                                   LocalTime previousTime,
                                                   LocalDate newDate,
                                                   LocalTime newTime,
                                                   RescheduleAppointmentRequest request) {
        ReschedulingHistory history = new ReschedulingHistory();

        history.setAppointment(appointment);
        history.setPreviousDate(previousDate);
        history.setPreviousTime(previousTime);
        history.setNewDate(newDate);
        history.setNewTime(newTime);
        history.setResponsible(normalizeOptionalText(request.getResponsible(), "Sistema"));
        history.setReason(normalizeOptionalText(request.getReason(), "Re-agendamiento de cita"));
        history.setChangeDate(LocalDateTime.now());

        return history;
    }

    private String buildReschedulingNotes(Appointment appointment, ReschedulingHistory history) {
        String currentNotes = appointment.getNotes() == null ? "" : appointment.getNotes().trim();

        String newNotes = "Re-agendada de "
                + history.getPreviousDate() + " " + history.getPreviousTime()
                + " a " + history.getNewDate() + " " + history.getNewTime()
                + ". Responsable: " + history.getResponsible()
                + ". Motivo: " + history.getReason();

        if (currentNotes.isBlank()) {
            return newNotes;
        }

        return currentNotes + " | " + newNotes;
    }

    private Map<String, Object> convertAppointmentToMap(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();

        response.put("id", appointment.getId());
        response.put("patientId", appointment.getPatient().getId());
        response.put("patient", appointment.getPatient().getFullName());
        response.put("document", appointment.getPatient().getDocumentNumber());
        response.put("doctorId", appointment.getDoctor().getId());
        response.put("doctor", appointment.getDoctor().getFullName());
        response.put("date", appointment.getDate());
        response.put("time", appointment.getTime());
        response.put("status", appointment.getStatus().name());
        response.put("notes", appointment.getNotes());

        return response;
    }

    private String normalizeOptionalText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value.trim().replaceAll("\\s+", " ");
    }
}
