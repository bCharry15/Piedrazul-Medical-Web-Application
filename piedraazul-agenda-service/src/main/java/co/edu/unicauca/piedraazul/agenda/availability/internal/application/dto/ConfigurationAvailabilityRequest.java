package co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class ConfigurationAvailabilityRequest {

    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalMinutes;
    private Integer weekWindow;

    public ConfigurationAvailabilityRequest() {
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    @JsonProperty("diaSemana")
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @JsonProperty("diaSemana")
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    @JsonProperty("intervaloMinutos")
    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    @JsonProperty("intervaloMinutos")
    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    @JsonProperty("ventanaSemanas")
    public Integer getWeekWindow() {
        return weekWindow;
    }

    @JsonProperty("ventanaSemanas")
    public void setWeekWindow(Integer weekWindow) {
        this.weekWindow = weekWindow;
    }
}
