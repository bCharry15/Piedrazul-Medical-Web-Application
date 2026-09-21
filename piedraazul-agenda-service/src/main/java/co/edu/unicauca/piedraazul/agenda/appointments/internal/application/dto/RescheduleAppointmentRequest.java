package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleAppointmentRequest {

    private LocalDate newDate;
    private LocalTime newTime;
    private String responsible;
    private String reason;

    public RescheduleAppointmentRequest() {
    }

    @JsonProperty("dateNueva")
    public LocalDate getNewDate() {
        return newDate;
    }

    @JsonProperty("dateNueva")
    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    @JsonProperty("timeNueva")
    public LocalTime getNewTime() {
        return newTime;
    }

    @JsonProperty("timeNueva")
    public void setNewTime(LocalTime newTime) {
        this.newTime = newTime;
    }

    @JsonProperty("responsable")
    public String getResponsible() {
        return responsible;
    }

    @JsonProperty("responsable")
    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    @JsonProperty("motivo")
    public String getReason() {
        return reason;
    }

    @JsonProperty("motivo")
    public void setReason(String reason) {
        this.reason = reason;
    }
}
