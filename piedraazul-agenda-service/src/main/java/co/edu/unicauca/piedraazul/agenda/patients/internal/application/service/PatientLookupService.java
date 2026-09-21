package co.edu.unicauca.piedraazul.agenda.patients.internal.application.service;

import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.patients.api.PatientLookup;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out.FindPatientPort;

@Service
public class PatientLookupService implements PatientLookup {

    private final FindPatientPort findPatientPort;

    public PatientLookupService(FindPatientPort findPatientPort) {
        this.findPatientPort = findPatientPort;
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return findPatientPort.existsByDocumentNumber(documentNumber);
    }
}