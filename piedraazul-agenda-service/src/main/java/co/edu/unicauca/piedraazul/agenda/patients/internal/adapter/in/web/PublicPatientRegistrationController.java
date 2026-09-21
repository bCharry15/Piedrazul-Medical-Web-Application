package co.edu.unicauca.piedraazul.agenda.patients.internal.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto.RegisterPatientRequest;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.dto.RegisterPatientResponse;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.in.RegisterPatientUseCase;

@RestController
@RequestMapping("/api/public/patients")
public class PublicPatientRegistrationController {

    private final RegisterPatientUseCase
            registerPatientUseCase;

    public PublicPatientRegistrationController(
            RegisterPatientUseCase registerPatientUseCase
    ) {
        this.registerPatientUseCase =
                registerPatientUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterPatientResponse register(
            @RequestBody
            RegisterPatientRequest request
    ) {

        return registerPatientUseCase
                .register(
                        request
                );
    }
}