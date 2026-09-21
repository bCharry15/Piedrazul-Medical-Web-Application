package co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model;
import java.time.LocalDate;

import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 100, unique = true)
    private String username;

    @Column(name = "numero_document", nullable = false, unique = true, length = 30)
    private String documentNumber;

    @Column(name = "tipo_document", nullable = false, length = 30)
    private String documentType;

    @Column(name = "nombres", nullable = false, length = 120)
    private String firstNames;

    @Column(name = "apellidos", nullable = false, length = 120)
    private String lastNames;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Column(name = "date_nacimiento")
    private LocalDate birthDate;

    @Column(name = "email", length = 150)
    private String email;

    public Patient() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty("documentNumber")
    public String getDocumentNumber() {
        return documentNumber;
    }

    @JsonProperty("documentType")
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

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }

    @JsonProperty("dateNacimiento")
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public String getFullName() {
        String firstNamesValue = firstNames != null ? firstNames.trim() : "";
        String lastNamesValue = lastNames != null ? lastNames.trim() : "";
        return (firstNamesValue + " " + lastNamesValue).trim();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
