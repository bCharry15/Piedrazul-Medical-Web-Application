package co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

public interface PatientRepository
        extends JpaRepository<Patient, Long> {

    Optional<Patient> findByDocumentNumber(
            String documentNumber
    );

    Optional<Patient> findByUsername(
            String username
    );

    boolean existsByDocumentNumber(
            String documentNumber
    );

    boolean existsByUsername(
            String username
    );

    boolean existsByEmailIgnoreCase(
            String email
    );
}