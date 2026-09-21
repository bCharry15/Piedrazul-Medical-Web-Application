package co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out.GetOrCreatePatientPort;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence.PatientRepository;

@Component
public class PatientPersistenceAdapter implements GetOrCreatePatientPort {

    private final PatientRepository patientRepository;

    public PatientPersistenceAdapter(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
        public Patient getOrCreatePatient(
            String documentNumber,
            String documentType,
            String firstNames,
            String lastNames,
            String phone,
            Gender gender,
            LocalDate birthDate,
            String email
    ) {
        return patientRepository.findByDocumentNumber(documentNumber)
                .orElseGet(() -> createPatient(
                        documentNumber,
                        documentType,
                firstNames,
                lastNames,
                        phone,
                        gender,
                birthDate,
                        email
                ));
    }

    private Patient createPatient(
            String documentNumber,
            String documentType,
            String firstNames,
            String lastNames,
            String phone,
            Gender gender,
            LocalDate birthDate,
            String email
    ) {
        Patient patient = new Patient();
        patient.setDocumentNumber(documentNumber);
        patient.setDocumentType(documentType);
        patient.setFirstNames(firstNames);
        patient.setLastNames(lastNames);
        patient.setPhone(phone);
        patient.setGender(gender);
        patient.setBirthDate(birthDate);
        patient.setEmail(email);

        return patientRepository.save(patient);
    }
}
