package co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class DoctorAvailabilityTest {

    @Test
    void shouldInitializeAsActive() {

        DoctorAvailability availability =
                new DoctorAvailability();

        assertNull(
                availability.getId()
        );

        assertNull(
                availability.getDoctor()
        );

        assertTrue(
                availability.getActive()
        );
    }

    @Test
    void shouldStoreAvailabilityInformation() {

        DoctorAvailability availability =
                new DoctorAvailability();

        availability.setId(
                10L
        );

        availability.setDoctor(
                null
        );

        availability.setDayOfWeek(
                DayOfWeek.MONDAY
        );

        availability.setStartTime(
                LocalTime.of(
                        8,
                        0
                )
        );

        availability.setEndTime(
                LocalTime.of(
                        12,
                        0
                )
        );

        availability.setIntervalMinutes(
                30
        );

        availability.setWeekWindow(
                4
        );

        availability.setActive(
                false
        );

        assertEquals(
                10L,
                availability.getId()
        );

        assertNull(
                availability.getDoctor()
        );

        assertEquals(
                DayOfWeek.MONDAY,
                availability.getDayOfWeek()
        );

        assertEquals(
                LocalTime.of(
                        8,
                        0
                ),
                availability.getStartTime()
        );

        assertEquals(
                LocalTime.of(
                        12,
                        0
                ),
                availability.getEndTime()
        );

        assertEquals(
                30,
                availability.getIntervalMinutes()
        );

        assertEquals(
                4,
                availability.getWeekWindow()
        );

        assertFalse(
                availability.getActive()
        );
    }
}