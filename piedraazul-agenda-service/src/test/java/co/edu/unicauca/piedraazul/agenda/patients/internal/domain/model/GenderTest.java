package co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class GenderTest {

    @Test
    void shouldContainExpectedValues() {

        assertArrayEquals(
                new Gender[]{
                        Gender.HOMBRE,
                        Gender.MUJER,
                        Gender.OTRO
                },
                Gender.values()
        );

        assertEquals(
                Gender.HOMBRE,
                Gender.valueOf(
                        "HOMBRE"
                )
        );

        assertEquals(
                Gender.MUJER,
                Gender.valueOf(
                        "MUJER"
                )
        );

        assertEquals(
                Gender.OTRO,
                Gender.valueOf(
                        "OTRO"
                )
        );
    }
}