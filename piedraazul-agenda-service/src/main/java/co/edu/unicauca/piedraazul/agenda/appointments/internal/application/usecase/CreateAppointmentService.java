package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;
import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentCommand;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.CreateAppointmentUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.CreateAppointmentPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.PublishAppointmentCreatedEventPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.FindDoctorAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out.GetOrCreatePatientPort;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@Service
public class CreateAppointmentService implements CreateAppointmentUseCase {

    private static final int DEFAULT_SCHEDULING_WINDOW_WEEKS = 4;

    private static final Set<LocalDate> FESTIVOS_COLOMBIA_2026 = Set.of(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 12),
            LocalDate.of(2026, 3, 23),
            LocalDate.of(2026, 4, 2),
            LocalDate.of(2026, 4, 3),
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 18),
            LocalDate.of(2026, 6, 8),
            LocalDate.of(2026, 6, 15),
            LocalDate.of(2026, 6, 29),
            LocalDate.of(2026, 7, 20),
            LocalDate.of(2026, 8, 7),
            LocalDate.of(2026, 8, 17),
            LocalDate.of(2026, 10, 12),
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 16),
            LocalDate.of(2026, 12, 8),
            LocalDate.of(2026, 12, 25)
    );

    private final FindDoctorPort findDoctorPort;
    private final FindDoctorAvailabilityPort findDoctorAvailabilityPort;
    private final GetOrCreatePatientPort getOrCreatePatientPort;
    private final CreateAppointmentPort createAppointmentPort;
    private final FindAppointmentsPort findAppointmentsPort;
    private final PublishAppointmentCreatedEventPort publishAppointmentCreatedEventPort;

    public CreateAppointmentService(FindDoctorPort findDoctorPort,
                            FindDoctorAvailabilityPort findDoctorAvailabilityPort,
                            GetOrCreatePatientPort getOrCreatePatientPort,
                            CreateAppointmentPort createAppointmentPort,
                            FindAppointmentsPort findAppointmentsPort,
                            PublishAppointmentCreatedEventPort publishAppointmentCreatedEventPort) {
        this.findDoctorPort = findDoctorPort;
        this.findDoctorAvailabilityPort = findDoctorAvailabilityPort;
        this.getOrCreatePatientPort = getOrCreatePatientPort;
        this.createAppointmentPort = createAppointmentPort;
        this.findAppointmentsPort = findAppointmentsPort;
        this.publishAppointmentCreatedEventPort = publishAppointmentCreatedEventPort;
    }

    @Override
    public CreateAppointmentResponse createAppointment(CreateAppointmentCommand command) {
        validateCreateAppointmentRequest(command);
        validateDateNotHoliday(command.getDate());

        Doctor doctor = findDoctorPort.findById(command.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un médico/terapista con id: " + command.getDoctorId()
                ));

        validateConfiguredAvailability(doctor, command);
        validateFirstGeneralConsultationAppointment(command, doctor);

        Gender gender = convertGender(command.getGender());

        Patient patient = getOrCreatePatientPort.getOrCreatePatient(
                normalizeSimpleText(command.getDocumentNumber()),
                normalizeDocumentType(command.getDocumentType()),
                normalizeNameOrLastName(command.getFirstNames()),
                normalizeNameOrLastName(command.getLastNames()),
                normalizeSimpleText(command.getPhone()),
                gender,
                command.getBirthDate(),
                normalizeEmail(command.getEmail())
        );

        validatePatientWithoutActiveAppointment(patient);

        try {
            Appointment createdAppointment = createAppointmentPort.createAppointment(
                    patient,
                    doctor,
                    command.getDate(),
                    command.getTime(),
                        normalizeOptionalText(command.getNotes())
            );

                    publishAppointmentCreatedEvent(createdAppointment);

            return new CreateAppointmentResponse(
                    createdAppointment.getId(),
                    createdAppointment.getPatient().getDocumentNumber(),
                    createdAppointment.getPatient().getFullName(),
                    createdAppointment.getDoctor().getFullName(),
                    createdAppointment.getDate(),
                    createdAppointment.getTime(),
                    createdAppointment.getStatus().name()
            );

        } catch (ResponseStatusException ex) {
            throw ex;

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo crear la cita. Detalle: " + ex.getMessage()
            );
        }
    }

    private void publishAppointmentCreatedEvent(Appointment createdAppointment) {
        try {
            publishAppointmentCreatedEventPort.publish(createdAppointment);
        } catch (Exception e) {
            System.err.println("AGENDA-SERVICE -> No se pudo publicar evento de cita creada: " + e.getMessage());
        }
    }

    private void validateCreateAppointmentRequest(CreateAppointmentCommand command) {
        if (command == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud no puede estar vacía."
            );
        }

        validateRequiredText(command.getDocumentNumber(), "El número de documento es obligatorio.");
        validateRequiredText(command.getDocumentType(), "El tipo de documento es obligatorio.");
        validateRequiredText(command.getFirstNames(), "Los nombres son obligatorios.");
        validateRequiredText(command.getLastNames(), "Los apellidos son obligatorios.");
        validateRequiredText(command.getPhone(), "El phone es obligatorio.");
        validateRequiredText(command.getGender(), "El género es obligatorio.");

        if (command.getDoctorId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El médico/terapista es obligatorio."
            );
        }

        if (command.getDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de la cita es obligatoria."
            );
        }

        if (command.getTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de la cita es obligatoria."
            );
        }
    }

    private void validateRequiredText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void validateDateNotHoliday(LocalDate date) {
        if (date == null) {
            return;
        }

        if (FESTIVOS_COLOMBIA_2026.contains(date)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede agendar una cita en día festivo en Colombia."
            );
        }
    }

    private void validateConfiguredAvailability(Doctor doctor, CreateAppointmentCommand command) {
        DayOfWeek dayOfWeek = command.getDate().getDayOfWeek();

        DoctorAvailability availability = findDoctorAvailabilityPort
                .findActiveAvailability(doctor, dayOfWeek)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El médico/terapista no tiene disponibilidad configurada para el día seleccionado."
                ));

        validateDateWithinWindow(command.getDate(), availability);
        validateTimeWithinSlot(command, availability);
        validateTimeInterval(command, availability);
    }

    private void validateDateWithinWindow(LocalDate date, DoctorAvailability availability) {
        LocalDate currentDate = LocalDate.now();

        int weekWindow = availability.getWeekWindow() != null && availability.getWeekWindow() > 0
            ? availability.getWeekWindow()
                : DEFAULT_SCHEDULING_WINDOW_WEEKS;

        LocalDate limitDate = currentDate.plusWeeks(weekWindow);

        if (date.isBefore(currentDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede crear una cita en una fecha pasada."
            );
        }

        if (date.isAfter(limitDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha supera la ventana de agendamiento permitida de "
                            + weekWindow + " semanas."
            );
        }
    }

    private void validateTimeWithinSlot(CreateAppointmentCommand command, DoctorAvailability availability) {
        if (availability.getStartTime() == null || availability.getEndTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La disponibilidad del médico/terapista está incompleta."
            );
        }

        if (command.getTime().isBefore(availability.getStartTime())
            || !command.getTime().isBefore(availability.getEndTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora seleccionada está fuera de la franja configurada para el médico/terapista."
            );
        }
    }

    private void validateTimeInterval(CreateAppointmentCommand command, DoctorAvailability availability) {
        if (availability.getIntervalMinutes() == null || availability.getIntervalMinutes() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El intervalo de atención del médico/terapista no está configurado correctamente."
            );
        }

        long minutesFromStart = ChronoUnit.MINUTES.between(
                availability.getStartTime(),
                command.getTime()
        );

        if (minutesFromStart < 0 || minutesFromStart % availability.getIntervalMinutes() != 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora seleccionada no coincide con el intervalo configurado de "
                            + availability.getIntervalMinutes() + " minutos."
            );
        }
    }

    private void validatePatientWithoutActiveAppointment(Patient patient) {
        if (patient == null || patient.getDocumentNumber() == null) {
            return;
        }

        List<Appointment> patientAppointments = findAppointmentsPort.findByPatientDocumentNumber(
                patient.getDocumentNumber()
        );

        boolean hasActiveAppointment = patientAppointments.stream()
            .anyMatch(appointment -> isActiveStatus(appointment.getStatus()));

        if (hasActiveAppointment) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El paciente ya tiene una cita agendada o pendiente. " +
                            "No puede crear una nueva cita hasta que la cita actual sea atendida, cancelada o marcada como no asistida."
            );
        }
    }

    private boolean isActiveStatus(AppointmentStatus status) {
        if (status == null) {
            return false;
        }

        String statusName = status.name();

        return "PROGRAMADA".equalsIgnoreCase(statusName)
            || "CONFIRMADA".equalsIgnoreCase(statusName)
            || "PENDIENTE".equalsIgnoreCase(statusName);
    }

    private void validateFirstGeneralConsultationAppointment(CreateAppointmentCommand command, Doctor selectedDoctor) {
        if (command == null || command.getDocumentNumber() == null || selectedDoctor == null) {
            return;
        }

        List<Appointment> patientAppointments = findAppointmentsPort.findByPatientDocumentNumber(
                normalizeSimpleText(command.getDocumentNumber())
        );

        boolean hasAttendedGeneralConsultationAppointment = patientAppointments.stream()
                .anyMatch(appointment -> appointment.getStatus() == AppointmentStatus.ATENDIDA
                        && appointment.getDoctor() != null
                        && isGeneralConsultation(appointment.getDoctor()));

        if (!hasAttendedGeneralConsultationAppointment && !isGeneralConsultation(selectedDoctor)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El paciente debe tener primero una cita de Consulta General atendida antes de agendar terapia neural, quiropraxia, fisioterapia u otra especialidad."
            );
        }
    }

    private boolean isGeneralConsultation(Doctor doctor) {
        if (doctor == null || doctor.getSpecialty() == null) {
            return false;
        }

        String specialty = removeAccents(doctor.getSpecialty())
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");

        return specialty.equals("medicina general")
            || specialty.equals("consulta general")
            || specialty.contains("medicina general")
            || specialty.contains("consulta general");
    }

    private String normalizeDocumentType(String documentType) {
        if (documentType == null || documentType.trim().isEmpty()) {
            return "CC";
        }

        return documentType.trim().toUpperCase();
    }

    private Gender convertGender(String gender) {
        try {
            return Gender.valueOf(gender.trim().toUpperCase());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Género inválido. Valores permitidos: HOMBRE, MUJER, OTRO."
            );
        }
    }

    private String normalizeNameOrLastName(String value) {
        if (value == null) {
            return "";
        }

        String unaccentedValue = removeAccents(value);
        String normalizedValue = unaccentedValue.trim().replaceAll("\\s+", " ");

        if (normalizedValue.isEmpty()) {
            return "";
        }

        String[] words = normalizedValue.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }

            result.append(Character.toUpperCase(word.charAt(0)));

            if (word.length() > 1) {
                result.append(word.substring(1));
            }

            result.append(" ");
        }

        return result.toString().trim();
    }

    private String normalizeSimpleText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private String removeAccents(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
