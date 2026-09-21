package co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    void shouldContainExpectedRoles() {

        assertArrayEquals(
                new UserRole[]{
                        UserRole.ADMIN,
                        UserRole.SCHEDULER,
                        UserRole.DOCTOR,
                        UserRole.PATIENT
                },
                UserRole.values()
        );

        assertEquals(
                UserRole.ADMIN,
                UserRole.valueOf(
                        "ADMIN"
                )
        );

        assertEquals(
                UserRole.SCHEDULER,
                UserRole.valueOf(
                        "SCHEDULER"
                )
        );

        assertEquals(
                UserRole.DOCTOR,
                UserRole.valueOf(
                        "DOCTOR"
                )
        );

        assertEquals(
                UserRole.PATIENT,
                UserRole.valueOf(
                        "PATIENT"
                )
        );
    }
}