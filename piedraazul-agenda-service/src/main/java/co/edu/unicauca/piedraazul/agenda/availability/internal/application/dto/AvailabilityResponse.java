package co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityResponse {

    private Long doctorId;
    private String doctor;
    private LocalDate date;
    private Integer intervalMinutes;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<LocalTime> availableSlots;

    public AvailabilityResponse() {
    }

    public AvailabilityResponse(Long doctorId, String doctor, LocalDate date,
                                  Integer intervalMinutes, LocalTime startTime,
                                  LocalTime endTime, List<LocalTime> availableSlots) {
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.date = date;
        this.intervalMinutes = intervalMinutes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.availableSlots = availableSlots;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonProperty("intervaloMinutos")
    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    @JsonProperty("intervaloMinutos")
    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    @JsonProperty("timeInicio")
    public LocalTime getStartTime() {
        return startTime;
    }

    @JsonProperty("timeInicio")
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("timeFin")
    public LocalTime getEndTime() {
        return endTime;
    }

    @JsonProperty("timeFin")
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("franjasDisponibles")
    public List<LocalTime> getAvailableSlots() {
        return availableSlots;
    }

    @JsonProperty("franjasDisponibles")
    public void setAvailableSlots(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots;
    }
}


