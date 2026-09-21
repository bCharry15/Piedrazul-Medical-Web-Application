package co.edu.unicauca.piedraazul.agenda.availability.internal.domain.service;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntervalAvailabilityStrategyTest {

    private IntervalAvailabilityStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new IntervalAvailabilityStrategy();
    }

    @Test
    void calculatesAvailableSlotsByInterval() {
        List<LocalTime> availableSlots = strategy.calculateAvailableSlots(
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                15,
                Set.of()
        );

        assertNotNull(availableSlots);
        assertFalse(availableSlots.isEmpty());
    }
}