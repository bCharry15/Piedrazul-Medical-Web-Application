package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patient;
    private Long doctorId;
    private String doctor;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String notes;

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long id, Long patientId, String patient, Long doctorId, String doctor,
                        LocalDate date, LocalTime time, String status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.patient = patient;
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatient() {
        return patient;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctor() {
        return doctor;
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

    public String getNotes() {
        return notes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}


