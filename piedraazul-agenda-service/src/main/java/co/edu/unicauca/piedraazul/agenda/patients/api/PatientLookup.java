package co.edu.unicauca.piedraazul.agenda.patients.api;

public interface PatientLookup {

    boolean existsByDocumentNumber(String documentNumber);
}