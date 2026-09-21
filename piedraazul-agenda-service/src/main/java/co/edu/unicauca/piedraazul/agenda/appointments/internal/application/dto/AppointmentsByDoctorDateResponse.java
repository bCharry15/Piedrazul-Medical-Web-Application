package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class AppointmentsByDoctorDateResponse {

    private Long doctorId;
    private String doctor;
    private LocalDate date;
    private long count;
    private List<AppointmentResponse> appointments;

    public AppointmentsByDoctorDateResponse() {
    }

    public AppointmentsByDoctorDateResponse(Long doctorId, String doctor, LocalDate date,
                                       long count, List<AppointmentResponse> appointments) {
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.date = date;
        this.count = count;
        this.appointments = appointments;
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

    @JsonProperty("cantidad")
    public long getCount() {
        return count;
    }

    public List<AppointmentResponse> getAppointments() {
        return appointments;
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

    @JsonProperty("cantidad")
    public void setCount(long count) {
        this.count = count;
    }

    public void setAppointments(List<AppointmentResponse> appointments) {
        this.appointments = appointments;
    }
}


