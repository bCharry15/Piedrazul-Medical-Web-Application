package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAppointmentResponse {

    private Long id;
    private String documentNumber;
    private String patientName;
    private String doctorName;
    private LocalDate date;
    private LocalTime time;
    private String status;

    public CreateAppointmentResponse() {
    }

    public CreateAppointmentResponse(
            Long id,
            String documentNumber,
            String patientName,
            String doctorName,
            LocalDate date,
            LocalTime time,
            String status
    ) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    @JsonProperty("numeroDocument")
    public String getDocumentNumber() {
        return documentNumber;
    }

    @JsonProperty("nombrePatient")
    public String getPatientName() {
        return patientName;
    }

    @JsonProperty("nombreDoctor")
    public String getDoctorName() {
        return doctorName;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }
}
