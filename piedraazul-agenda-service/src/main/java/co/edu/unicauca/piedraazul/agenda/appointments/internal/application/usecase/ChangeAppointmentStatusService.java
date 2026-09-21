package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.ChangeAppointmentStatusUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.ChangeAppointmentStatusRequest;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import jakarta.transaction.Transactional;

@Service
public class ChangeAppointmentStatusService implements ChangeAppointmentStatusUseCase {

    private final FindAppointmentsPort findAppointmentsPort;

    public ChangeAppointmentStatusService(FindAppointmentsPort findAppointmentsPort) {
        this.findAppointmentsPort = findAppointmentsPort;
    }

    @Override
    @Transactional
    public Map<String, Object> changeStatus(Long appointmentId, ChangeAppointmentStatusRequest request) {
        validateIdAppointment(appointmentId);
        validateRequest(request);

        Appointment appointment = findAppointmentsPort.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una cita con id: " + appointmentId
                ));

        AppointmentStatus newStatus = convertStatus(request.getStatus());

        validateStatusChange(appointment.getStatus(), newStatus);

        AppointmentStatus previousStatus = appointment.getStatus();

        appointment.setStatus(newStatus);

        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            appointment.setNotes(buildStatusChangeNotes(appointment.getNotes(), previousStatus, newStatus, request.getNotes()));
        }

        Appointment updatedAppointment = findAppointmentsPort.save(appointment);

        Map<String, Object> response = convertAppointmentToMap(updatedAppointment);
        response.put("mensaje", "Estado de la cita actualizado correctamente.");
        response.put("statusAnterior", previousStatus.name());
        response.put("statusNuevo", newStatus.name());

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

    private void validateRequest(ChangeAppointmentStatusRequest request) {
        if (request == null || request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo estado de la cita es obligatorio."
            );
        }
    }

    private AppointmentStatus convertStatus(String status) {
        try {
            return AppointmentStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado inválido. Valores permitidos: CONFIRMADA, ATENDIDA, CANCELADA, NO_VINO, COMPLETADA."
            );
        }
    }

    private void validateStatusChange(AppointmentStatus currentStatus, AppointmentStatus newStatus) {
        if (newStatus == AppointmentStatus.PROGRAMADA || newStatus == AppointmentStatus.PENDIENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se permite devolver una cita a estado PROGRAMADA o PENDIENTE desde este endpoint."
            );
        }

        if (currentStatus == AppointmentStatus.ATENDIDA
            || currentStatus == AppointmentStatus.COMPLETADA
            || currentStatus == AppointmentStatus.CANCELADA
            || currentStatus == AppointmentStatus.NO_VINO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cita ya está en un estado final y no puede ser modificada."
            );
        }

        if (newStatus != AppointmentStatus.CONFIRMADA
            && newStatus != AppointmentStatus.ATENDIDA
            && newStatus != AppointmentStatus.COMPLETADA
            && newStatus != AppointmentStatus.CANCELADA
            && newStatus != AppointmentStatus.NO_VINO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado no permitido para cambio de cita."
            );
        }
    }

    private String buildStatusChangeNotes(String currentNotes,
                                          AppointmentStatus previousStatus,
                                          AppointmentStatus newStatus,
                                          String newNotes) {
        String baseNotes = currentNotes == null ? "" : currentNotes.trim();

        String changeText = "Cambio de estado de "
                + previousStatus.name()
                + " a "
                + newStatus.name()
                + ". Observación: "
                + newNotes.trim();

        if (baseNotes.isBlank()) {
            return changeText;
        }

        return baseNotes + " | " + changeText;
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
}
