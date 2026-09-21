package co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.in;
import java.util.List;
import java.util.Map;

import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;

public interface ManageUsersUseCase {

    Map<String, Object> login(String username, String password);

    Map<String, String> register(String username, String password, String role);

    Map<String, String> generateTemporaryPassword(String username);

    Map<String, String> resetPasswordSafely(String username,
                                                   String documentNumber,
                                                   String newPassword);

    List<User> listByRole(String role);
}
