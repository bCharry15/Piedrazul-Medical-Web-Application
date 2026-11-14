package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;

@Service
public class MonthlyAppointmentReportService {

    private final AppointmentRepository appointmentRepository;

    public MonthlyAppointmentReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<MonthlyTotal> getReport(Long doctorId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = startDate.plusYears(1);

        long[] totalsByMonth = new long[12];
        for (var row : appointmentRepository.countMonthlyByDoctor(doctorId, startDate, endDate)) {
            totalsByMonth[row.getMonth() - 1] = row.getTotal();
        }

        List<MonthlyTotal> report = new ArrayList<>(12);
        for (int month = 1; month <= 12; month++) {
            report.add(new MonthlyTotal(month, totalsByMonth[month - 1]));
        }

        return report;
    }

    public record MonthlyTotal(int month, long total) {}
}