package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.GetAppointmentsByDoctorDateUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentsByDoctorDateResponse;

@Service
public class GetAppointmentsByDoctorDateService implements GetAppointmentsByDoctorDateUseCase {

    private final FindDoctorPort findDoctorPort;
    private final FindAppointmentsPort findAppointmentsPort;

    public GetAppointmentsByDoctorDateService(FindDoctorPort findDoctorPort,
                                               FindAppointmentsPort findAppointmentsPort) {
        this.findDoctorPort = findDoctorPort;
        this.findAppointmentsPort = findAppointmentsPort;
    }

    @Override
    public AppointmentsByDoctorDateResponse get(Long doctorId, LocalDate date) {
        if (doctorId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id del médico/terapista es obligatorio."
            );
        }

        if (date == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de consulta es obligatoria."
            );
        }

        Doctor doctor = findDoctorPort.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un médico/terapista con id: " + doctorId
                ));

        List<Appointment> appointments = findAppointmentsPort.findByDoctorAndDate(doctor, date);

        List<AppointmentResponse> appointmentsResponse = appointments.stream()
                .map(this::convertToAppointmentResponse)
                .toList();

        return new AppointmentsByDoctorDateResponse(
                doctor.getId(),
                doctor.getFullName(),
                date,
                appointmentsResponse.size(),
                appointmentsResponse
        );
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
