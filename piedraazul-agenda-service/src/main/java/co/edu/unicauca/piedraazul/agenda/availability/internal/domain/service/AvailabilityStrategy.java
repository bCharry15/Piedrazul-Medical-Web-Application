package co.edu.unicauca.piedraazul.agenda.availability.internal.domain.service;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface AvailabilityStrategy {

    List<LocalTime> calculateAvailableSlots(
            LocalTime startTime,
            LocalTime endTime,
            Integer intervalMinutes,
            Set<LocalTime> occupiedTimes
    );
}


