package co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

public class CreateAvailabilityRequest {

    private Long doctorId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalMinutes;
    private Integer weekWindow;
    private Boolean active;

    public CreateAvailabilityRequest() {
    }

    public Long getDoctorId() {
        return doctorId;
    }

    @JsonProperty("diaSemana")
    public String getDayOfWeek() {
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

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    @JsonProperty("diaSemana")
    public void setDayOfWeek(String dayOfWeek) {
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


