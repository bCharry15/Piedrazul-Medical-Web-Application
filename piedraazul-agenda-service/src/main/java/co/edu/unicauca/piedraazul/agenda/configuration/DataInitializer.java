package co.edu.unicauca.piedraazul.agenda.configuration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence.DoctorAvailabilityRepository;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.identity.internal.adapter.out.persistence.UserRepository;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.SynchronizeUsersKeycloakService;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.service.factory.UserFactory;
import co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence.PatientRepository;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Map<String, String> BASE_PASSWORDS = Map.of(
            "admin", "admin123",
            "scheduler", "scheduler123",
            "doctor", "doctor123",
            "patient", "patient123"
    );

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final UserFactory userFactory;
    private final SynchronizeUsersKeycloakService synchronizeUsersKeycloakService;

    public DataInitializer(DoctorRepository doctorRepository,
                           DoctorAvailabilityRepository doctorAvailabilityRepository,
                           UserRepository userRepository,
                           PatientRepository patientRepository,
                           UserFactory userFactory,
                           SynchronizeUsersKeycloakService synchronizeUsersKeycloakService) {
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.userFactory = userFactory;
        this.synchronizeUsersKeycloakService = synchronizeUsersKeycloakService;
    }

    @Override
    public void run(String... args) {
        createAdminIfAbsent();
        createSchedulerIfAbsent();

        User patientUser = getOrCreatePatientUser();
        createPatientProfileIfAbsent(patientUser);

        User doctorUser = getOrCreateDoctorUser();

        Doctor doctor = getOrCreateDoctor(
        "Jhoiner Puentes",
        "Consulta General",
        15
);

        associateUserWithDoctor(doctor, doctorUser);

        createAvailabilityIfAbsent(
            doctor,
                DayOfWeek.MONDAY,
            LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                15,
                4
        );

        createAvailabilityIfAbsent(
            doctor,
                DayOfWeek.SUNDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                15,
                4
        );

        synchronizeLocalUsersWithKeycloak();

        System.out.println("AGENDA-SERVICE -> Datos iniciales cargados correctamente en piedraazul_agenda");
    }

    private void createAdminIfAbsent() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        User admin = userFactory.createAdmin("admin", "admin123");
        userRepository.save(admin);

        System.out.println("AGENDA-SERVICE -> Admin inicial creado en BD usando UserFactory.");
    }

    private void createSchedulerIfAbsent() {
        if (userRepository.findByUsername("scheduler").isPresent()) {
            return;
        }

        User scheduler = userFactory.createScheduler("scheduler", "scheduler123");
        userRepository.save(scheduler);

        System.out.println("AGENDA-SERVICE -> Scheduler inicial creado en BD usando UserFactory.");
    }

    private User getOrCreatePatientUser() {
        return userRepository.findByUsername("patient")
                .orElseGet(() -> {
                    User patient = userFactory.createPatient("patient", "patient123");
                    User savedPatient = userRepository.save(patient);

                    System.out.println("AGENDA-SERVICE -> Usuario paciente inicial creado en BD usando UserFactory.");

                    return savedPatient;
                });
    }

    private void createPatientProfileIfAbsent(User patientUser) {
        if (patientRepository.findByUsername(patientUser.getUsername()).isPresent()) {
            return;
        }

        Patient existingPatientByDocument = patientRepository
                .findByDocumentNumber("1234567890")
                .orElse(null);

        if (existingPatientByDocument != null) {
            existingPatientByDocument.setUsername(patientUser.getUsername());
            patientRepository.save(existingPatientByDocument);

            System.out.println("AGENDA-SERVICE -> Perfil paciente existente asociado al usuario paciente.");

            return;
        }

        Patient patient = new Patient();
        patient.setUsername(patientUser.getUsername());
        patient.setDocumentNumber("1234567890");
        patient.setDocumentType("CC");
        patient.setFirstNames("Patient");
        patient.setLastNames("Demo");
        patient.setPhone("3001234567");
        patient.setGender(Gender.HOMBRE);
        patient.setBirthDate(LocalDate.of(2000, 1, 1));
        patient.setEmail("patient.demo@piedraazul.com");

        patientRepository.save(patient);

        System.out.println("AGENDA-SERVICE -> Perfil inicial de paciente creado en BD.");
    }

    private User getOrCreateDoctorUser() {
        return userRepository.findByUsername("doctor")
                .orElseGet(() -> {
                    User doctor = userFactory.createDoctor("doctor", "doctor123");
                    User savedDoctor = userRepository.save(doctor);

                    System.out.println("AGENDA-SERVICE -> Usuario médico inicial creado en BD usando UserFactory.");

                    return savedDoctor;
                });
    }

    private Doctor getOrCreateDoctor(String fullName,
                                    String specialty,
                                       Integer intervalMinutes) {
        List<Doctor> doctors = doctorRepository.findAll();

        for (Doctor doctor : doctors) {
            if (doctor.getFullName() != null
                    && doctor.getFullName().equalsIgnoreCase(fullName)) {
                doctor.setSpecialty(specialty);
                doctor.setIntervalMinutes(intervalMinutes);
                return doctorRepository.save(doctor);
            }
        }

        Doctor doctor = new Doctor();
        doctor.setFullName(fullName);
        doctor.setSpecialty(specialty);
        doctor.setIntervalMinutes(intervalMinutes);

        return doctorRepository.save(doctor);
    }

    private void associateUserWithDoctor(Doctor doctor, User doctorUser) {
        if (doctor.getUser() != null
                && doctor.getUser().getUsername() != null
                && doctor.getUser().getUsername().equalsIgnoreCase(doctorUser.getUsername())) {
            return;
        }

        doctor.setUser(doctorUser);
        doctorRepository.save(doctor);

        System.out.println("AGENDA-SERVICE -> Usuario médico asociado al perfil de Jhoiner Puentes.");
    }

    private void createAvailabilityIfAbsent(Doctor doctor,
                                               DayOfWeek dayOfWeek,
                                               LocalTime startTime,
                                               LocalTime endTime,
                                               Integer intervalMinutes,
                                               Integer weekWindow) {
        boolean exists = doctorAvailabilityRepository
            .findByDoctorAndActiveTrue(doctor)
                .stream()
                .anyMatch(availability ->
                availability.getDayOfWeek() == dayOfWeek
                                && availability.getStartTime().equals(startTime)
                                && availability.getEndTime().equals(endTime)
                );
        if (exists) {
            return;
        }

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setDayOfWeek(dayOfWeek);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setIntervalMinutes(intervalMinutes);
        availability.setWeekWindow(weekWindow);
        availability.setActive(true);

        doctorAvailabilityRepository.save(availability);
    }

    private void synchronizeLocalUsersWithKeycloak() {
    List<User> users = userRepository.findAll();

    for (User user : users) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            continue;
        }

        if (user.getRole() == null) {
            continue;
        }

        String username = user.getUsername().trim();

        /*
         * Only the system base users are synchronized automatically.
         * Users created from the application must not be synchronized here because
         * the locally stored password is encrypted or may not match Keycloak.
         */
        if (!BASE_PASSWORDS.containsKey(username)) {
            System.out.println("AGENDA-SERVICE -> Usuario omitido en sincronización automática con Keycloak: " + username);
            continue;
        }

        String password = BASE_PASSWORDS.get(username);

        synchronizeUsersKeycloakService.synchronizeUserOnStartup(
                username,
                password,
                user.getRole()
        );
    }
}

    private String getPasswordForSynchronization(String username, UserRole role) {
        String normalizedUsername = username.trim();

        if (BASE_PASSWORDS.containsKey(normalizedUsername)) {
            return BASE_PASSWORDS.get(normalizedUsername);
        }

        return SynchronizeUsersKeycloakService.RECOVERY_PASSWORD;
    }
}
