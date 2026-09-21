package co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;

public interface ManageUsersPort {

    Optional<User> findByUsername(String username);

    List<User> listByRole(UserRole role);

    User save(User user);
}
