package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

@Service
public class WeeklyAppointmentReportService {

    private final AppointmentRepository appointmentRepository;

    public WeeklyAppointmentReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public WeeklyReport getReport(Long doctorId, LocalDate date) {
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endExclusive = weekStart.plusWeeks(1);

        var totalsByStatus = new EnumMap<AppointmentStatus, Long>(AppointmentStatus.class);
        for (var row : appointmentRepository.countByDoctorAndDateRangeGroupedByStatus(
                doctorId, weekStart, endExclusive)) {
            totalsByStatus.put(row.getStatus(), row.getTotal());
        }

        List<StatusTotal> statuses = Arrays.stream(AppointmentStatus.values())
                .map(status -> new StatusTotal(status, totalsByStatus.getOrDefault(status, 0L)))
                .toList();

        long total = statuses.stream().mapToLong(StatusTotal::total).sum();
        long attended = totalsByStatus.getOrDefault(AppointmentStatus.ATENDIDA, 0L);

        return new WeeklyReport(weekStart, endExclusive.minusDays(1), total, attended, statuses);
    }

    public record StatusTotal(AppointmentStatus status, long total) {}

    public record WeeklyReport(
            LocalDate weekStart,
            LocalDate weekEnd,
            long total,
            long attended,
            List<StatusTotal> statuses
    ) {}
}