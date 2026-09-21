package co.edu.unicauca.piedraazul.agenda.identity.internal.adapter.out.persistence;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.ManageUsersPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.adapter.out.persistence.UserRepository;

@Component
public class UserPersistenceAdapter implements ManageUsersPort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> listByRole(UserRole role) {
        return userRepository.findByRoleOrderByUsernameAsc(role);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
