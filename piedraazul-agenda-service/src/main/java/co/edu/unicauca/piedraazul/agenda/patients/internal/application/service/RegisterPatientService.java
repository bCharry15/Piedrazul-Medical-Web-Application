package co.edu.unicauca.piedraazul.agenda.patients.internal.application.service;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.in.ManageUsersUseCase;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.RegisterUserKeycloakPort;
import co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence.PatientRepository;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto.RegisterPatientRequest;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto.RegisterPatientResponse;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.in.RegisterPatientUseCase;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@Service
public class RegisterPatientService
        implements RegisterPatientUseCase {

    private final PatientRepository patientRepository;

    private final ManageUsersUseCase manageUsersUseCase;

    private final RegisterUserKeycloakPort registerUserKeycloakPort;

    public RegisterPatientService(
            PatientRepository patientRepository,
            ManageUsersUseCase manageUsersUseCase,
            RegisterUserKeycloakPort registerUserKeycloakPort
    ) {
        this.patientRepository =
                patientRepository;

        this.manageUsersUseCase =
                manageUsersUseCase;

        this.registerUserKeycloakPort =
                registerUserKeycloakPort;
    }

    @Override
    @Transactional
    public RegisterPatientResponse register(
            RegisterPatientRequest request
    ) {

        validateRequest(request);

        String username =
                normalizeUsername(
                        request.username()
                );

        String documentNumber =
                normalizeDocument(
                        request.documentNumber()
                );

        String documentType =
                request
                        .documentType()
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        String nombres =
                normalizeName(
                        request.nombres()
                );

        String apellidos =
                normalizeName(
                        request.apellidos()
                );

        String email =
                normalizeEmail(
                        request.email()
                );

        String phone =
                request
                        .phone()
                        .trim();

        Gender gender =
                convertGender(
                        request.gender()
                );

        validateDuplicates(
                username,
                documentNumber,
                email
        );

        /*
         * El usuario público SIEMPRE
         * será PATIENT.
         *
         * Nunca recibimos el rol
         * desde Angular.
         */
        manageUsersUseCase.register(
                username,
                request.password(),
                "PATIENT"
        );

        Patient patient =
                new Patient();

        patient.setUsername(
                username
        );

        patient.setDocumentNumber(
                documentNumber
        );

        patient.setDocumentType(
                documentType
        );

        patient.setFirstNames(
                nombres
        );

        patient.setLastNames(
                apellidos
        );

        patient.setEmail(
                email
        );

        patient.setPhone(
                phone
        );

        patient.setGender(
                gender
        );

        patient.setBirthDate(
                request.dateNacimiento()
        );

        Patient savedPatient;

        try {

            savedPatient =
                    patientRepository.save(
                            patient
                    );

        } catch (
                RuntimeException ex
        ) {

            /*
             * Compensación:
             *
             * si Keycloak fue creado pero
             * falla el perfil del paciente,
             * deshabilitamos esa cuenta.
             */

            try {

                registerUserKeycloakPort
                        .disableUser(
                                username
                        );

            } catch (
                    Exception compensationError
            ) {

                System.err.println(
                        "No fue posible deshabilitar "
                        + "el usuario en Keycloak "
                        + "después del error de registro: "
                        + username
                        + ". Detalle: "
                        + compensationError
                                .getMessage()
                );
            }

            throw ex;
        }

        return new RegisterPatientResponse(
                savedPatient.getId(),
                savedPatient.getUsername(),
                savedPatient.getFirstNames(),
                savedPatient.getLastNames(),
                savedPatient.getDocumentType(),
                savedPatient.getDocumentNumber(),
                savedPatient.getEmail(),
                savedPatient.getPhone(),
                savedPatient
                        .getGender()
                        .name(),
                savedPatient
                        .getBirthDate() != null
                        ? savedPatient
                                .getBirthDate()
                                .toString()
                        : null,
                "Cuenta creada correctamente. "
                        + "Ya puede iniciar sesión en PiedraAzul."
        );
    }

    private void validateRequest(
            RegisterPatientRequest request
    ) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud de registro está vacía."
            );
        }

        validateRequiredText(
                request.username(),
                "El nombre de usuario es obligatorio."
        );

        validateRequiredText(
                request.password(),
                "La contraseña es obligatoria."
        );

        validateRequiredText(
                request.confirmPassword(),
                "Debe confirmar la contraseña."
        );

        validateRequiredText(
                request.nombres(),
                "Los nombres son obligatorios."
        );

        validateRequiredText(
                request.apellidos(),
                "Los apellidos son obligatorios."
        );

        validateRequiredText(
                request.documentType(),
                "El tipo de documento es obligatorio."
        );

        validateRequiredText(
                request.documentNumber(),
                "El número de documento es obligatorio."
        );

        validateRequiredText(
                request.email(),
                "El correo electrónico es obligatorio."
        );

        validateRequiredText(
                request.phone(),
                "El teléfono es obligatorio."
        );

        validateRequiredText(
                request.gender(),
                "El género es obligatorio."
        );

        if (
                !request
                        .password()
                        .equals(
                                request
                                        .confirmPassword()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las contraseñas no coinciden."
            );
        }

        validatePassword(
                request.password()
        );

        validateUsername(
                request.username()
        );

        validateEmail(
                request.email()
        );

        validateBirthDate(
                request.dateNacimiento()
        );
    }

    private void validateDuplicates(
            String username,
            String documentNumber,
            String email
    ) {

        if (
                patientRepository
                        .existsByUsername(
                                username
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya está registrado."
            );
        }

        if (
                patientRepository
                        .existsByDocumentNumber(
                                documentNumber
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El número de documento ya está registrado."
            );
        }

        if (
                patientRepository
                        .existsByEmailIgnoreCase(
                                email
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo electrónico ya está registrado."
            );
        }
    }

    private void validateUsername(
            String username
    ) {

        String normalized =
                normalizeUsername(
                        username
                );

        if (
                !normalized.matches(
                        "[a-z0-9._-]{4,50}"
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario debe tener entre 4 y 50 caracteres "
                            + "y solo puede contener letras, números, "
                            + "punto, guion o guion bajo."
            );
        }
    }

    private void validatePassword(
            String password
    ) {

        if (
                password == null
                || password.length() < 8
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 8 caracteres."
            );
        }

        boolean uppercase =
                password.matches(
                        ".*[A-Z].*"
                );

        boolean lowercase =
                password.matches(
                        ".*[a-z].*"
                );

        boolean number =
                password.matches(
                        ".*[0-9].*"
                );

        if (
                !uppercase
                || !lowercase
                || !number
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe contener al menos "
                            + "una mayúscula, una minúscula y un número."
            );
        }
    }

    private void validateEmail(
            String email
    ) {

        String normalized =
                normalizeEmail(
                        email
                );

        if (
                !normalized.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo electrónico no tiene un formato válido."
            );
        }
    }

    private void validateBirthDate(
            LocalDate birthDate
    ) {

        if (
                birthDate != null
                && birthDate.isAfter(
                        LocalDate.now()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de nacimiento no puede estar en el futuro."
            );
        }
    }

    private Gender convertGender(
            String gender
    ) {

        try {

            return Gender.valueOf(
                    gender
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            )
            );

        } catch (
                Exception ex
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Género inválido. Valores permitidos: "
                            + "HOMBRE, MUJER, OTRO."
            );
        }
    }

    private String normalizeUsername(
            String username
    ) {

        return username
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String normalizeDocument(
            String documentNumber
    ) {

        return documentNumber
                .trim()
                .replaceAll(
                        "[^0-9A-Za-z]",
                        ""
                );
    }

    private String normalizeEmail(
            String email
    ) {

        return email
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String normalizeName(
            String value
    ) {

        String normalizedValue =
                value
                        .trim()
                        .replaceAll(
                                "\\s+",
                                " "
                        );

        String[] words =
                normalizedValue
                        .toLowerCase(
                                Locale.ROOT
                        )
                        .split(" ");

        StringBuilder result =
                new StringBuilder();

        for (
                String word :
                words
        ) {

            if (
                    word.isBlank()
            ) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (
                    word.length() > 1
            ) {

                result.append(
                        word.substring(1)
                );
            }

            result.append(" ");
        }

        return result
                .toString()
                .trim();
    }

    private void validateRequiredText(
            String value,
            String message
    ) {

        if (
                value == null
                || value
                        .trim()
                        .isEmpty()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }
    }
}