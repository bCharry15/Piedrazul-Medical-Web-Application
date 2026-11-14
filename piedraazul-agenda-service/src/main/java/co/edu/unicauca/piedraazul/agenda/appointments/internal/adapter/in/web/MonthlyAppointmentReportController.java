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

@RestController
@RequestMapping("/api/appointments/reports")
public class MonthlyAppointmentReportController {

    private final MonthlyAppointmentReportService reportService;
    private final DoctorRepository doctorRepository;

    public MonthlyAppointmentReportController(
            MonthlyAppointmentReportService reportService,
            DoctorRepository doctorRepository
    ) {
        this.reportService = reportService;
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

        String username = jwt.getClaimAsString("preferred_username");
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sin perfil médico activo");
        }

        var doctor = doctorRepository.findByUserUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Usuario sin perfil médico activo"));

        return reportService.getReport(doctor.getId(), year);
    }
}