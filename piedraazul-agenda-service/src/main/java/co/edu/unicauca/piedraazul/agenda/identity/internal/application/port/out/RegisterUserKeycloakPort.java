package co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out;

public interface RegisterUserKeycloakPort {

    void registerUser(
            String username,
            String password,
            String role
    );

    void updatePassword(
            String username,
            String newPassword,
            boolean temporary
    );

    void disableUser(String username);
}