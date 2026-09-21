package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAppointmentRequest {

    private String documentNumber;
    private String documentType;
    private String firstNames;
    private String lastNames;
    private String phone;
    private String gender;
    private LocalDate birthDate;
    private String email;

    private Long doctorId;
    private LocalDate date;
    private LocalTime time;
    private String notes;

    public CreateAppointmentRequest() {
    }

    @JsonProperty("numeroDocument")
    public String getDocumentNumber() {
        return documentNumber;
    }

    @JsonProperty("tipoDocument")
    public String getDocumentType() {
        return documentType;
    }

    @JsonProperty("nombres")
    public String getFirstNames() {
        return firstNames;
    }

    @JsonProperty("apellidos")
    public String getLastNames() {
        return lastNames;
    }

    @JsonProperty("celular")
    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    @JsonProperty("dateNacimiento")
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @JsonProperty("correo")
    public String getEmail() {
        return email;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getNotes() {
        return notes;
    }

    @JsonProperty("numeroDocument")
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @JsonProperty("tipoDocument")
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("nombres")
    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    @JsonProperty("apellidos")
    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    @JsonProperty("celular")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("dateNacimiento")
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @JsonProperty("correo")
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}


