package co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out.FindPatientPort;

@Component
public class FindPatientPersistenceAdapter implements FindPatientPort {

    private final PatientRepository patientRepository;

    public FindPatientPersistenceAdapter(
            PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return patientRepository.existsByDocumentNumber(documentNumber);
    }
}