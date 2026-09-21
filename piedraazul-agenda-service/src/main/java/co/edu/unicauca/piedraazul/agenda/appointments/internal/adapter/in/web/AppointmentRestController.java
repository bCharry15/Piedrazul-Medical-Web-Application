package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.in.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentsByDoctorDateResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.ChangeAppointmentStatusRequest;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentCommand;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.RescheduleAppointmentRequest;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.ChangeAppointmentStatusUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.CreateAppointmentUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.GetAppointmentsByDoctorDateUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.GetPatientAppointmentsUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.GetReschedulingHistoryUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.RescheduleAppointmentUseCase;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentRestController {

    private final GetAppointmentsByDoctorDateUseCase getAppointmentsByDoctorDateUseCase;
    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetPatientAppointmentsUseCase getAppointmentsPatientUseCase;
    private final ChangeAppointmentStatusUseCase changeAppointmentStatusUseCase;
    private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;
    private final GetReschedulingHistoryUseCase getReschedulingHistoryUseCase;

    public AppointmentRestController(
            GetAppointmentsByDoctorDateUseCase getAppointmentsByDoctorDateUseCase,
            CreateAppointmentUseCase createAppointmentUseCase,
            GetPatientAppointmentsUseCase getAppointmentsPatientUseCase,
            ChangeAppointmentStatusUseCase changeAppointmentStatusUseCase,
            RescheduleAppointmentUseCase rescheduleAppointmentUseCase,
            GetReschedulingHistoryUseCase getReschedulingHistoryUseCase
    ) {
        this.getAppointmentsByDoctorDateUseCase =
                getAppointmentsByDoctorDateUseCase;

        this.createAppointmentUseCase =
                createAppointmentUseCase;

        this.getAppointmentsPatientUseCase =
                getAppointmentsPatientUseCase;

        this.changeAppointmentStatusUseCase =
                changeAppointmentStatusUseCase;

        this.rescheduleAppointmentUseCase =
                rescheduleAppointmentUseCase;

        this.getReschedulingHistoryUseCase =
                getReschedulingHistoryUseCase;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AppointmentsByDoctorDateResponse listByDoctorAndDate(
            @RequestParam Long doctorId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return getAppointmentsByDoctorDateUseCase.get(
                doctorId,
                date
        );
    }

    @GetMapping("/patient/{documentNumber}")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentResponse> listByPatient(
            @PathVariable String documentNumber
    ) {
        return getAppointmentsPatientUseCase
                .getByDocumentNumber(
                        documentNumber
                );
    }

    @GetMapping(
            value = {"/export", "/exportar"},
            produces = "text/csv"
    )
    public ResponseEntity<String> exportAppointmentsByDoctorAndDate(
            @RequestParam Long doctorId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        AppointmentsByDoctorDateResponse response =
                getAppointmentsByDoctorDateUseCase.get(
                        doctorId,
                        date
                );

        StringBuilder csv =
                new StringBuilder();

        /*
         * BOM UTF-8.
         * Ayuda a que Excel reconozca correctamente
         * caracteres como:
         *
         * á, é, í, ó, ú, ñ
         */
        csv.append('\uFEFF');

        /*
         * Le indica a Excel que el separador
         * de columnas es punto y coma.
         *
         * Esto es especialmente útil en configuraciones
         * regionales como Colombia.
         */
        csv.append("sep=;\r\n");

        csv.append(
                "ID Appointment;"
                        + "Patient;"
                        + "Doctor;"
                        + "Date;"
                        + "Time;"
                        + "Status;"
                        + "Notes"
                        + "\r\n"
        );

        response.getAppointments()
                .forEach(appointment -> {

                    csv.append(
                            appointment.getId()
                    ).append(";");

                    csv.append(
                            escapeCsv(
                                    appointment.getPatient()
                            )
                    ).append(";");

                    csv.append(
                            escapeCsv(
                                    appointment.getDoctor()
                            )
                    ).append(";");

                    csv.append(
                            appointment.getDate()
                    ).append(";");

                    csv.append(
                            appointment.getTime()
                    ).append(";");

                    csv.append(
                            escapeCsv(
                                    appointment.getStatus()
                            )
                    ).append(";");

                    csv.append(
                            escapeCsv(
                                    appointment.getNotes()
                            )
                    ).append("\r\n");
                });

        String fileName =
                "appointments_doctor_"
                        + doctorId
                        + "_"
                        + date
                        + ".csv";

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "text/csv; charset=UTF-8"
                )
                .body(
                        csv.toString()
                );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAppointmentResponse createAppointment(
            @RequestBody CreateAppointmentCommand command
    ) {
        return createAppointmentUseCase
                .createAppointment(
                        command
                );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeAppointmentStatus(
            @PathVariable Long id,
            @RequestBody ChangeAppointmentStatusRequest request
    ) {

        return ResponseEntity.ok(
                changeAppointmentStatusUseCase
                        .changeStatus(
                                id,
                                request
                        )
        );
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Map<String, Object>> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody RescheduleAppointmentRequest request
    ) {

        return ResponseEntity.ok(
                rescheduleAppointmentUseCase
                        .reschedule(
                                id,
                                request
                        )
        );
    }

    @GetMapping("/{id}/history-reschedulings")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> listReschedulingHistory(
            @PathVariable Long id
    ) {

        return getReschedulingHistoryUseCase
                .getByAppointmentId(
                        id
                );
    }

    private String escapeCsv(
            String value
    ) {

        if (value == null) {
            return "";
        }

        /*
         * Las comillas dobles dentro de un campo CSV
         * deben duplicarse.
         */
        String escapedValue =
                value.replace(
                        "\"",
                        "\"\""
                );

        /*
         * Como ahora usamos ; como separador,
         * cualquier campo que contenga:
         *
         * ;
         * "
         * salto de línea
         *
         * debe ir entre comillas.
         */
        if (
                escapedValue.contains(";")
                || escapedValue.contains("\"")
                || escapedValue.contains("\n")
                || escapedValue.contains("\r")
        ) {

            return "\""
                    + escapedValue
                    + "\"";
        }

        return escapedValue;
    }
}