package co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.in;

import co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto.RegisterPatientRequest;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto.RegisterPatientResponse;

public interface RegisterPatientUseCase {

    RegisterPatientResponse register(
            RegisterPatientRequest request
    );
}