package co.edu.unicauca.piedraazul.agenda.availability.internal.domain.service;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class IntervalAvailabilityStrategy implements AvailabilityStrategy {

    @Override
    public List<LocalTime> calculateAvailableSlots(
            LocalTime startTime,
            LocalTime endTime,
            Integer intervalMinutes,
            Set<LocalTime> occupiedTimes
    ) {
        List<LocalTime> availableSlots = new ArrayList<>();

        LocalTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            LocalTime normalizedTime = currentTime.truncatedTo(ChronoUnit.SECONDS);

            if (!occupiedTimes.contains(normalizedTime)) {
                availableSlots.add(normalizedTime);
            }

            currentTime = currentTime.plusMinutes(intervalMinutes);
        }

        return availableSlots;
    }
}


