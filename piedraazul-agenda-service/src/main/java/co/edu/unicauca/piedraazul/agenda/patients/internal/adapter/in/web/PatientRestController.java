package co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.in.web;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence.PatientRepository;

@RestController
public class PatientRestController {

    private final PatientRepository patientRepository;

    public PatientRestController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping({"/api/patients/profile/{username}", "/api/patients/perfil/{username}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getProfileByUsername(@PathVariable String username) {
        validateRequiredText(username, "El nombre de usuario es obligatorio.");

        Patient patient = patientRepository.findByUsername(username.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un perfil de paciente asociado al usuario."
                ));

        return convertPatientToResponse(patient);
    }

    @GetMapping("/api/patients/document/{documentNumber}")
@ResponseStatus(HttpStatus.OK)
public Map<String, Object> getPatientByDocument(@PathVariable String documentNumber) {
    validateRequiredText(documentNumber, "El número de documento es obligatorio.");

    Patient patient = patientRepository.findByDocumentNumber(documentNumber.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe un paciente con ese número de documento."
            ));

    return convertPatientToResponse(patient);
}

    @PutMapping({"/api/patients/profile", "/api/patients/perfil"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> saveProfile(@RequestBody Map<String, Object> body) {
        String username = getText(body, "username");
        String documentNumber = getText(body, "documentNumber");
        String documentType = getText(body, "documentType");
        String firstNames = getText(body, "nombres");
        String lastNames = getText(body, "apellidos");
        String phone = getText(body, "phone");
        String genderText = getText(body, "gender");
        String birthDateText = getOptionalText(body, "dateNacimiento");
        String email = getOptionalText(body, "email");

        validateRequiredText(username, "El nombre de usuario es obligatorio.");
        validateRequiredText(documentNumber, "El número de documento es obligatorio.");
        validateRequiredText(documentType, "El tipo de documento es obligatorio.");
        validateRequiredText(firstNames, "Los nombres son obligatorios.");
        validateRequiredText(lastNames, "Los apellidos son obligatorios.");
        validateRequiredText(phone, "El phone es obligatorio.");
        validateRequiredText(genderText, "El género es obligatorio.");

        username = username.trim();
        documentNumber = documentNumber.trim();

        Patient patientByUsername = patientRepository.findByUsername(username)
                .orElse(null);

        Patient patientByDocument = patientRepository.findByDocumentNumber(documentNumber)
                .orElse(null);

        if (patientByDocument != null
            && patientByUsername != null
            && !patientByDocument.getId().equals(patientByUsername.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El número de documento ya está asociado a otro perfil de paciente."
            );
        }

        if (patientByDocument != null
            && patientByUsername == null
            && patientByDocument.getUsername() != null
            && !patientByDocument.getUsername().isBlank()
            && !patientByDocument.getUsername().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El número de documento ya está asociado a otro usuario."
            );
        }

        Patient patient = patientByUsername != null
            ? patientByUsername
            : patientByDocument;

        if (patient == null) {
            patient = new Patient();
        }

        patient.setUsername(username);
        patient.setDocumentNumber(documentNumber);
        patient.setDocumentType(documentType.trim());
        patient.setFirstNames(normalizeName(firstNames));
        patient.setLastNames(normalizeName(lastNames));
        patient.setPhone(phone.trim());
        patient.setGender(convertGender(genderText));
        patient.setBirthDate(parseDate(birthDateText));
        patient.setEmail(normalizeEmail(email));

        Patient savedPatient = patientRepository.save(patient);

        return convertPatientToResponse(savedPatient);
    }

    private Map<String, Object> convertPatientToResponse(Patient patient) {
        Map<String, Object> response = new HashMap<>();

        response.put("id", patient.getId());
        response.put("username", patient.getUsername());
        response.put("documentNumber", patient.getDocumentNumber());
        response.put("documentType", patient.getDocumentType());
        response.put("nombres", patient.getFirstNames());
        response.put("apellidos", patient.getLastNames());
        response.put("phone", patient.getPhone());
        response.put("gender", patient.getGender() != null ? patient.getGender().name() : null);
        response.put("dateNacimiento", patient.getBirthDate() != null ? patient.getBirthDate().toString() : null);
        response.put("email", patient.getEmail());

        return response;
    }

    private String getText(Map<String, Object> body, String field) {
        Object value = body.get(field);

        if (value == null) {
            return "";
        }

        return value.toString().trim();
    }

    private String getOptionalText(Map<String, Object> body, String field) {
        Object value = body.get(field);

        if (value == null) {
            return null;
        }

        String text = value.toString().trim();

        return text.isEmpty() ? null : text;
    }

    private void validateRequiredText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Gender convertGender(String gender) {
        try {
            return Gender.valueOf(gender.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Género inválido. Valores permitidos: HOMBRE, MUJER, OTRO."
            );
        }
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(date.trim());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de nacimiento debe tener formato yyyy-MM-dd."
            );
        }
    }

    private String normalizeName(String value) {
        if (value == null) {
            return "";
        }

        String normalizedValue = value.trim().replaceAll("\\s+", " ");

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

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return email.trim().toLowerCase();
    }
}
