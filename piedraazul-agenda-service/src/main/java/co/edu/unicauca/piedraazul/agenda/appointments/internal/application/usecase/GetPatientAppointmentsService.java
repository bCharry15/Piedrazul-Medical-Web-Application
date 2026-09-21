package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.GetPatientAppointmentsUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;

@Service
public class GetPatientAppointmentsService implements GetPatientAppointmentsUseCase {

    private final FindAppointmentsPort findAppointmentsPort;

    public GetPatientAppointmentsService(FindAppointmentsPort findAppointmentsPort) {
        this.findAppointmentsPort = findAppointmentsPort;
    }

    @Override
    public List<AppointmentResponse> getByDocumentNumber(String documentNumber) {
        return findAppointmentsPort.findByPatientDocumentNumber(documentNumber)
                .stream()
                .map(this::convertToAppointmentResponse)
                .toList();
    }

    private AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();

        response.setId(appointment.getId());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatient(appointment.getPatient().getFullName());
        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctor(appointment.getDoctor().getFullName());
        response.setDate(appointment.getDate());
        response.setTime(appointment.getTime());
        response.setStatus(appointment.getStatus().name());
        response.setNotes(appointment.getNotes());

        return response;
    }
}
