package co.edu.unicauca.piedraazul.agenda.identity.internal.domain.service.factory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;

@Component
public class UserFactory {

    private final BCryptPasswordEncoder passwordEncoder;

    public UserFactory(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createAdmin(String username, String plainPassword) {
        return createUser(username, plainPassword, UserRole.ADMIN);
    }

    public User createDoctor(String username, String plainPassword) {
        return createUser(username, plainPassword, UserRole.DOCTOR);
    }

    public User createPatient(String username, String plainPassword) {
        return createUser(username, plainPassword, UserRole.PATIENT);
    }

    public User createScheduler(String username, String plainPassword) {
        return createUser(username, plainPassword, UserRole.SCHEDULER);
    }

    private User createUser(String username, String plainPassword, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
