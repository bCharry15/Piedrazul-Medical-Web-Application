package co.edu.unicauca.piedraazul.agenda.identity.internal.domain.service.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;

class UserFactoryTest {

    private BCryptPasswordEncoder passwordEncoder;

    private UserFactory userFactory;

    @BeforeEach
    void setUp() {

        passwordEncoder =
                new BCryptPasswordEncoder();

        userFactory =
                new UserFactory(
                        passwordEncoder
                );
    }

    @Test
    void shouldCreateAdminUser() {

        String username =
                "admin_test";

        String plainPassword =
                "Admin123";

        User user =
                userFactory.createAdmin(
                        username,
                        plainPassword
                );

        assertUser(
                user,
                username,
                plainPassword,
                UserRole.ADMIN
        );
    }

    @Test
    void shouldCreateDoctorUser() {

        String username =
                "doctor_test";

        String plainPassword =
                "Doctor123";

        User user =
                userFactory.createDoctor(
                        username,
                        plainPassword
                );

        assertUser(
                user,
                username,
                plainPassword,
                UserRole.DOCTOR
        );
    }

    @Test
    void shouldCreatePatientUser() {

        String username =
                "patient_test";

        String plainPassword =
                "Patient123";

        User user =
                userFactory.createPatient(
                        username,
                        plainPassword
                );

        assertUser(
                user,
                username,
                plainPassword,
                UserRole.PATIENT
        );
    }

    @Test
    void shouldCreateSchedulerUser() {

        String username =
                "scheduler_test";

        String plainPassword =
                "Scheduler123";

        User user =
                userFactory.createScheduler(
                        username,
                        plainPassword
                );

        assertUser(
                user,
                username,
                plainPassword,
                UserRole.SCHEDULER
        );
    }

    private void assertUser(
            User user,
            String expectedUsername,
            String plainPassword,
            UserRole expectedRole
    ) {

        assertNotNull(
                user
        );

        assertEquals(
                expectedUsername,
                user.getUsername()
        );

        assertEquals(
                expectedRole,
                user.getRole()
        );

        assertEquals(
                UserStatus.ACTIVE,
                user.getStatus()
        );

        assertNotNull(
                user.getPassword()
        );

        assertNotEquals(
                plainPassword,
                user.getPassword()
        );

        assertTrue(
                passwordEncoder.matches(
                        plainPassword,
                        user.getPassword()
                )
        );
    }
}