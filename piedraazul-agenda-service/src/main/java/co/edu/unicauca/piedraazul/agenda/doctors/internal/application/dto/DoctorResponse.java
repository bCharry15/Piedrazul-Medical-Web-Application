package co.edu.unicauca.piedraazul.agenda.doctors.internal.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
public class DoctorResponse {

    private Long id;
    private String fullName;
    private String specialty;
    private Integer intervalMinutes;
    private String username;

    public DoctorResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("nombreCompleto")
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("nombreCompleto")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("especialidad")
    public String getSpecialty() {
        return specialty;
    }

    @JsonProperty("especialidad")
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @JsonProperty("intervalMinutes")
    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    @JsonProperty("intervalMinutes")
    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
