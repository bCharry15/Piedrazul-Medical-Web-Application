package co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto;

public record RegisterPatientResponse(
        Long id,
        String username,
        String nombres,
        String apellidos,
        String documentType,
        String documentNumber,
        String email,
        String phone,
        String gender,
        String dateNacimiento,
        String mensaje
) {
}