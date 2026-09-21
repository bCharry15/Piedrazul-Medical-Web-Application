package co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

public class DoctorAvailabilityResponse {

    private Long id;
    private Long doctorId;
    private String doctor;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalMinutes;
    private Integer weekWindow;
    private Boolean active;

    public DoctorAvailabilityResponse() {
    }

    public DoctorAvailabilityResponse(Long id, Long doctorId, String doctor, String dayOfWeek,
                                        LocalTime startTime, LocalTime endTime,
                                        Integer intervalMinutes, Integer weekWindow,
                                        Boolean active) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalMinutes = intervalMinutes;
        this.weekWindow = weekWindow;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctor() {
        return doctor;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
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


