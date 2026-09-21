package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.mapper;
import java.time.LocalDate;
import java.time.LocalTime;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;

public class AppointmentResponseBuilder {

    private Long id;
    private Long patientId;
    private String patient;
    private Long doctorId;
    private String doctor;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String notes;

    private AppointmentResponseBuilder() {
    }

    public static AppointmentResponseBuilder builder() {
        return new AppointmentResponseBuilder();
    }

    public AppointmentResponseBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public AppointmentResponseBuilder patientId(Long patientId) {
        this.patientId = patientId;
        return this;
    }

    public AppointmentResponseBuilder patient(String patient) {
        this.patient = patient;
        return this;
    }

    public AppointmentResponseBuilder doctorId(Long doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public AppointmentResponseBuilder doctor(String doctor) {
        this.doctor = doctor;
        return this;
    }

    public AppointmentResponseBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public AppointmentResponseBuilder time(LocalTime time) {
        this.time = time;
        return this;
    }

    public AppointmentResponseBuilder status(String status) {
        this.status = status;
        return this;
    }

    public AppointmentResponseBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    public AppointmentResponse build() {
        return new AppointmentResponse(
                id,
                patientId,
                patient,
                doctorId,
                doctor,
                date,
                time,
                status,
                notes
        );
    }
}


