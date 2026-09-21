package co.edu.unicauca.piedraazul.agenda.identity.internal.adapter.in.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.in.ManageUsersUseCase;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final ManageUsersUseCase manageUsersUseCase;

    public AuthRestController(
            ManageUsersUseCase manageUsersUseCase
    ) {
        this.manageUsersUseCase =
                manageUsersUseCase;
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> body
    ) {

        return manageUsersUseCase.login(
                body.get("username"),
                body.get("password")
        );
    }

    /*
     * Registro administrativo genérico.
     *
     * Este endpoint NO debe ser público.
     * SecurityConfig lo restringirá al rol ADMIN.
     */
    @PostMapping("/register")
    public Map<String, String> register(
            @RequestBody Map<String, String> body
    ) {

        return manageUsersUseCase.register(
                body.get("username"),
                body.get("password"),
                body.get("role")
        );
    }

    /*
     * Registro específico de agendadores.
     *
     * El frontend NO envía el rol.
     * El backend fuerza siempre SCHEDULER.
     */
    @PostMapping("/admin/schedulers")
    public Map<String, String> registerScheduler(
            @RequestBody Map<String, String> body
    ) {

        return manageUsersUseCase.register(
                body.get("username"),
                body.get("password"),
                "SCHEDULER"
        );
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(
            @RequestBody Map<String, String> body
    ) {

        return manageUsersUseCase
                .generateTemporaryPassword(
                        body.get("username")
                );
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(
            @RequestBody Map<String, String> body
    ) {

        return manageUsersUseCase
                .resetPasswordSafely(
                        body.get("username"),
                        body.get("documentNumber"),
                        body.get("nuevaPassword")
                );
    }

    /*
     * Permite al administrador consultar usuarios
     * por rol.
     *
     * Ejemplo:
     * GET /api/auth/users/role/SCHEDULER
     */
    @GetMapping("/users/role/{role}")
    public List<Map<String, Object>> listUsersByRole(
            @PathVariable String role
    ) {

        return manageUsersUseCase
                .listByRole(role)
                .stream()
                .map(this::convertUserToResponse)
                .toList();
    }

    private Map<String, Object> convertUserToResponse(
            User user
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "id",
                user.getId()
        );

        response.put(
                "username",
                user.getUsername()
        );

        response.put(
                "role",
                user.getRole() != null
                        ? user.getRole().name()
                        : null
        );

        response.put(
                "status",
                user.getStatus() != null
                        ? user.getStatus().name()
                        : null
        );

        return response;
    }
}