package co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "availabilities_doctor")
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "interval_minutes", nullable = false)
    private Integer intervalMinutes;

    @Column(name = "week_window", nullable = false)
    private Integer weekWindow;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public DoctorAvailability() {
    }

    public Long getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    @JsonProperty("diaSemana")
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @JsonProperty("timeInicio")
    public LocalTime getStartTime() {
        return startTime;
    }

    @JsonProperty("timeFin")
    public LocalTime getEndTime() {
        return endTime;
    }

    @JsonProperty("intervaloMinutos")
    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    @JsonProperty("ventanaSemanas")
    public Integer getWeekWindow() {
        return weekWindow;
    }

    @JsonProperty("activo")
    public Boolean getActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @JsonProperty("diaSemana")
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @JsonProperty("timeInicio")
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("timeFin")
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("intervaloMinutos")
    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    @JsonProperty("ventanaSemanas")
    public void setWeekWindow(Integer weekWindow) {
        this.weekWindow = weekWindow;
    }

    @JsonProperty("activo")
    public void setActive(Boolean active) {
        this.active = active;
    }
}


