package co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String fullName;

    @Column(name = "especialidad", nullable = false, length = 100)
    private String specialty;

    @Column(name = "interval_minutes", nullable = false)
    private Integer intervalMinutes;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Doctor() {
    }

    @PrePersist
    public void prePersist() {
        if (active == null) {
            active = true;
        }
    }

    public Long getId() {
        return id;
    }

    /*
     * This setter is useful when the object is built from DTOs or tests.
     * In JPA, the database normally assigns the id.
     */
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

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return fullName + " - " + specialty;
    }
}
