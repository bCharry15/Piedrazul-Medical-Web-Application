package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase.MonthlyAppointmentReportService;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase.MonthlyAppointmentReportService.MonthlyTotal;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase.WeeklyAppointmentReportService;

@RestController
@RequestMapping("/api/appointments/reports")
public class MonthlyAppointmentReportController {

    private final MonthlyAppointmentReportService reportService;
    private final WeeklyAppointmentReportService weeklyReportService;
    private final DoctorRepository doctorRepository;

    public MonthlyAppointmentReportController(
            MonthlyAppointmentReportService reportService,
            WeeklyAppointmentReportService weeklyReportService,
            DoctorRepository doctorRepository
    ) {
        this.reportService = reportService;
        this.weeklyReportService = weeklyReportService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<MonthlyTotal> getMonthlyReport(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("year") int year
    ) {
        if (year < 1900 || year > 9998) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Año fuera del rango permitido");
        }

                return reportService.getReport(requireActiveDoctorId(jwt), year);
    }

    @GetMapping("/weekly")
    @PreAuthorize("hasRole('DOCTOR')")
    public WeeklyAppointmentReportService.WeeklyReport getWeeklyReport(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date.getYear() < 1900 || date.getYear() > 9998) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Año fuera del rango permitido");
        }

        return weeklyReportService.getReport(requireActiveDoctorId(jwt), date);
    }

    private Long requireActiveDoctorId(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sin perfil médico activo");
        }

        return doctorRepository.findByUserUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Usuario sin perfil médico activo"))
                .getId();
    }
}