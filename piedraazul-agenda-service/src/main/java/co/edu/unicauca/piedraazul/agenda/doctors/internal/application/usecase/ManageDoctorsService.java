package co.edu.unicauca.piedraazul.agenda.doctors.internal.application.usecase;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;
import co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence.DoctorAvailabilityRepository;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.dto.DoctorRequest;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.in.ManageDoctorsUseCase;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.ManageDoctorsPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.SynchronizeUsersKeycloakService;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.EncodePasswordPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.ManageUsersPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;
import jakarta.transaction.Transactional;

@Service
public class ManageDoctorsService
        implements ManageDoctorsUseCase {

    private final ManageDoctorsPort
            manageDoctorsPort;

    private final ManageUsersPort
            manageUsersPort;

    private final EncodePasswordPort
            encodePasswordPort;

    private final SynchronizeUsersKeycloakService
            synchronizeUsersKeycloakService;

    private final DoctorRepository
            doctorRepository;

    private final AppointmentRepository
            appointmentRepository;

    private final DoctorAvailabilityRepository
            doctorAvailabilityRepository;

    public ManageDoctorsService(
            ManageDoctorsPort manageDoctorsPort,
            ManageUsersPort manageUsersPort,
            EncodePasswordPort encodePasswordPort,
            SynchronizeUsersKeycloakService synchronizeUsersKeycloakService,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            DoctorAvailabilityRepository doctorAvailabilityRepository
    ) {

        this.manageDoctorsPort =
                manageDoctorsPort;

        this.manageUsersPort =
                manageUsersPort;

        this.encodePasswordPort =
                encodePasswordPort;

        this.synchronizeUsersKeycloakService =
                synchronizeUsersKeycloakService;

        this.doctorRepository =
                doctorRepository;

        this.appointmentRepository =
                appointmentRepository;

        this.doctorAvailabilityRepository =
                doctorAvailabilityRepository;
    }

    @Override
    public List<Doctor> listAll() {

        return manageDoctorsPort
                .listAll();
    }

    @Override
    public Doctor createDoctor(
            DoctorRequest request
    ) {

        validateCreateRequest(
                request
        );

        String fullName =
                request
                        .getFullName()
                        .trim();

        String specialty =
                request
                        .getSpecialty()
                        .trim();

        String username =
                request
                        .getUsername()
                        .trim();

        String password =
                request
                        .getPassword();

        if (
                manageUsersPort
                        .findByUsername(
                                username
                        )
                        .isPresent()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese nombre de usuario."
            );
        }

        synchronizeUsersKeycloakService
                .synchronizeUserRequired(
                        username,
                        password,
                        UserRole.DOCTOR
                );

        User user =
                new User();

        user.setUsername(
                username
        );

        user.setPassword(
                encodePasswordPort
                        .encode(
                                password
                        )
        );

        user.setRole(
                UserRole.DOCTOR
        );

        user.setStatus(
                UserStatus.ACTIVE
        );

        User savedUser =
                manageUsersPort
                        .save(
                                user
                        );

        Doctor doctor =
                new Doctor();

        doctor.setFullName(
                fullName
        );

        doctor.setSpecialty(
                specialty
        );

        doctor.setIntervalMinutes(
                request.getIntervalMinutes()
                        != null
                        ? request.getIntervalMinutes()
                        : 15
        );

        doctor.setActive(
                true
        );

        doctor.setUser(
                savedUser
        );

        return manageDoctorsPort
                .save(
                        doctor
                );
    }

    @Override
    public Doctor getById(
            Long doctorId
    ) {

        if (doctorId == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id del médico es obligatorio."
            );
        }

        return manageDoctorsPort
                .findById(
                        doctorId
                )
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe un médico/terapista activo con id: "
                                                + doctorId
                                )
                );
    }

    @Override
    public Doctor updateDoctor(
            Long doctorId,
            DoctorRequest request
    ) {

        if (doctorId == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id del médico es obligatorio."
            );
        }

        validateUpdateRequest(
                request
        );

        Doctor doctor =
                doctorRepository
                        .findByIdAndActiveTrue(
                                doctorId
                        )
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "No existe un médico/terapista activo con id: "
                                                        + doctorId
                                        )
                        );

        doctor.setFullName(
                request
                        .getFullName()
                        .trim()
        );

        doctor.setSpecialty(
                request
                        .getSpecialty()
                        .trim()
        );

        doctor.setIntervalMinutes(
                request
                        .getIntervalMinutes()
        );

        return doctorRepository
                .save(
                        doctor
                );
    }

    @Override
    @Transactional
    public void deleteDoctor(
            Long doctorId
    ) {

        if (doctorId == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id del médico es obligatorio."
            );
        }

        Doctor doctor =
                doctorRepository
                        .findByIdAndActiveTrue(
                                doctorId
                        )
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "No existe un médico/terapista activo con id: "
                                                        + doctorId
                                        )
                        );

        long preservedAppointmentCount =
                appointmentRepository
                        .countByDoctorId(
                                doctorId
                        );

        User userDoctor =
                doctor.getUser();

        /*
         * Primero preparamos la eliminación lógica
         * en nuestra base de datos.
         */
        doctor.setActive(
                false
        );

        if (userDoctor != null) {

            userDoctor.setStatus(
                    UserStatus.INACTIVE
            );

            manageUsersPort
                    .save(
                            userDoctor
                    );
        }

        List<DoctorAvailability> availabilities =
                doctorAvailabilityRepository
                        .findByDoctorId(
                                doctorId
                        );

        for (
                DoctorAvailability availability :
                availabilities
        ) {

            availability.setActive(
                    false
            );
        }

        doctorAvailabilityRepository
                .saveAll(
                        availabilities
                );

        doctorRepository
                .save(
                        doctor
                );

        /*
         * El usuario también debe quedar
         * inhabilitado en Keycloak.
         *
         * Si esta operación falla, se lanza una
         * excepción y la transacción local se
         * revierte.
         */
        if (
                userDoctor != null
                        && userDoctor
                        .getUsername()
                        != null
                        && !userDoctor
                        .getUsername()
                        .trim()
                        .isEmpty()
        ) {

            synchronizeUsersKeycloakService
                    .disableUserRequired(
                            userDoctor
                                    .getUsername()
                    );
        }

        System.out.println(
                "AGENDA-SERVICE -> Médico desactivado por administrador. ID: "
                        + doctorId
                        + ". Appointments conservadas asociadas: "
                        + preservedAppointmentCount
        );
    }

    private void validateCreateRequest(
            DoctorRequest request
    ) {

        validateBaseRequest(
                request
        );

        if (
                request.getUsername()
                        == null
                        || request
                        .getUsername()
                        .trim()
                        .isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre de usuario del médico es obligatorio."
            );
        }

        if (
                request.getPassword()
                        == null
                        || request
                        .getPassword()
                        .trim()
                        .isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña del médico es obligatoria."
            );
        }

        if (
                request
                        .getPassword()
                        .trim()
                        .length()
                        < 6
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña del médico debe tener mínimo 6 caracteres."
            );
        }
    }

    private void validateUpdateRequest(
            DoctorRequest request
    ) {

        validateBaseRequest(
                request
        );
    }

    private void validateBaseRequest(
            DoctorRequest request
    ) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud no puede estar vacía."
            );
        }

        if (
                request.getFullName()
                        == null
                        || request
                        .getFullName()
                        .trim()
                        .isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre completo del médico es obligatorio."
            );
        }

        if (
                request.getSpecialty()
                        == null
                        || request
                        .getSpecialty()
                        .trim()
                        .isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La especialidad del médico es obligatoria."
            );
        }

        if (
                request.getIntervalMinutes()
                        == null
                        || request
                        .getIntervalMinutes()
                        <= 0
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El intervalo de atención debe ser mayor que cero."
            );
        }
    }
}