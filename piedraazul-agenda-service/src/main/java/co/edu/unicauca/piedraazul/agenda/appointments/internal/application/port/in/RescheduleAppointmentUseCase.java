package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import java.util.Map;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.RescheduleAppointmentRequest;

public interface RescheduleAppointmentUseCase {

    /*
     * requesterDoctorId != null  -> quien reagenda es un médico:
     *                               solo puede mover sus propias citas.
     * requesterDoctorId == null  -> admin / agendador / paciente
     *                               (comportamiento actual).
     */
    Map<String, Object> reschedule(
            Long appointmentId,
            RescheduleAppointmentRequest request,
            Long requesterDoctorId
    );

    default Map<String, Object> reschedule(
            Long appointmentId,
            RescheduleAppointmentRequest request
    ) {
        return reschedule(appointmentId, request, null);
    }
}
