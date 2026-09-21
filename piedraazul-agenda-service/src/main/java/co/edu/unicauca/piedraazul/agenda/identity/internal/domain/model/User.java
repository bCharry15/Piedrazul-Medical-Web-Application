package co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.unicauca.piedraazul.agenda.shared.observer.Observer;
import co.edu.unicauca.piedraazul.agenda.shared.observer.Subject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Transient
    private final Subject subject = new Subject();

    public void attach(Observer observer) {
        subject.attach(observer);
    }

    public void detach(Observer observer) {
        subject.detach(observer);
    }

    public void notifyObservers(String message) {
        subject.notifyObservers(message);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;

        subject.notifyObservers(
                "Estado del usuario '" +
                this.username +
                "' cambió a: " +
                status
        );
    }
}