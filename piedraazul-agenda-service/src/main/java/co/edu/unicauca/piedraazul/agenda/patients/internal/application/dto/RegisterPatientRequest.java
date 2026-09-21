package co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto;

import java.time.LocalDate;

public record RegisterPatientRequest(
        String username,
        String password,
        String confirmPassword,
        String nombres,
        String apellidos,
        String documentType,
        String documentNumber,
        String email,
        String phone,
        String gender,
        LocalDate dateNacimiento
) {
}