package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "history_reschedulings")
public class ReschedulingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "date_anterior", nullable = false)
    private LocalDate previousDate;

    @Column(name = "time_anterior", nullable = false)
    private LocalTime previousTime;

    @Column(name = "date_nueva", nullable = false)
    private LocalDate newDate;

    @Column(name = "time_nueva", nullable = false)
    private LocalTime newTime;

    @Column(name = "responsable", length = 120)
    private String responsible;

    @Column(name = "motivo", length = 255)
    private String reason;

    @Column(name = "date_cambio", nullable = false)
    private LocalDateTime changeDate;

    public ReschedulingHistory() {
    }

    public Long getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public LocalDate getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(LocalDate previousDate) {
        this.previousDate = previousDate;
    }

    public LocalTime getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(LocalTime previousTime) {
        this.previousTime = previousTime;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public LocalTime getNewTime() {
        return newTime;
    }

    public void setNewTime(LocalTime newTime) {
        this.newTime = newTime;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}
