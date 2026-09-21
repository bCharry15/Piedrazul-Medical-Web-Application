package co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out;

public interface FindPatientPort {

    boolean existsByDocumentNumber(String documentNumber);
}