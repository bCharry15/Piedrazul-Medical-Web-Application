package co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out;
import java.time.LocalDate;

import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;

public interface GetOrCreatePatientPort {

        Patient getOrCreatePatient(
            String documentNumber,
            String documentType,
            String firstNames,
            String lastNames,
            String phone,
            Gender gender,
            LocalDate birthDate,
            String email
    );
}
