package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@ExtendWith(MockitoExtension.class)
class GetPatientAppointmentsServiceTest {

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    private GetPatientAppointmentsService service;

    @BeforeEach
    void setUp() {

        service =
                new GetPatientAppointmentsService(
                        findAppointmentsPort
                );
    }

    @Test
    void shouldReturnEmptyListWhenPatientHasNoAppointments() {

        String documentNumber =
                "1075792407";

        when(
                findAppointmentsPort
                        .findByPatientDocumentNumber(
                                documentNumber
                        )
        ).thenReturn(
                List.of()
        );

        List<AppointmentResponse> result =
                service.getByDocumentNumber(
                        documentNumber
                );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );

        verify(
                findAppointmentsPort
        ).findByPatientDocumentNumber(
                documentNumber
        );
    }

    @Test
    void shouldConvertPatientAppointmentsToResponse() {

        String documentNumber =
                "1075792407";

        LocalDate appointmentDate =
                LocalDate.of(
                        2026,
                        9,
                        25
                );

        LocalTime appointmentTime =
                LocalTime.of(
                        10,
                        30
                );


        Patient patient =
                mock(
                        Patient.class
                );

        when(
                patient.getId()
        ).thenReturn(
                10L
        );

        when(
                patient.getFullName()
        ).thenReturn(
                "Brayan Chary"
        );


        Doctor doctor =
                mock(
                        Doctor.class
                );

        when(
                doctor.getId()
        ).thenReturn(
                20L
        );

        when(
                doctor.getFullName()
        ).thenReturn(
                "Yeison Vela"
        );


        Appointment appointment =
                mock(
                        Appointment.class
                );

        when(
                appointment.getId()
        ).thenReturn(
                8L
        );

        when(
                appointment.getPatient()
        ).thenReturn(
                patient
        );

        when(
                appointment.getDoctor()
        ).thenReturn(
                doctor
        );

        when(
                appointment.getDate()
        ).thenReturn(
                appointmentDate
        );

        when(
                appointment.getTime()
        ).thenReturn(
                appointmentTime
        );

        when(
                appointment.getStatus()
        ).thenReturn(
                AppointmentStatus.PROGRAMADA
        );

        when(
                appointment.getNotes()
        ).thenReturn(
                "Dolor en el tobillo izquierdo"
        );


        when(
                findAppointmentsPort
                        .findByPatientDocumentNumber(
                                documentNumber
                        )
        ).thenReturn(
                List.of(
                        appointment
                )
        );


        List<AppointmentResponse> result =
                service.getByDocumentNumber(
                        documentNumber
                );


        assertNotNull(
                result
        );

        assertEquals(
                1,
                result.size()
        );


        AppointmentResponse response =
                result.get(
                        0
                );


        assertEquals(
                8L,
                response.getId()
        );

        assertEquals(
                10L,
                response.getPatientId()
        );

        assertEquals(
                "Brayan Chary",
                response.getPatient()
        );

        assertEquals(
                20L,
                response.getDoctorId()
        );

        assertEquals(
                "Yeison Vela",
                response.getDoctor()
        );

        assertEquals(
                appointmentDate,
                response.getDate()
        );

        assertEquals(
                appointmentTime,
                response.getTime()
        );

        assertEquals(
                "PROGRAMADA",
                response.getStatus()
        );

        assertEquals(
                "Dolor en el tobillo izquierdo",
                response.getNotes()
        );


        verify(
                findAppointmentsPort
        ).findByPatientDocumentNumber(
                documentNumber
        );
    }
}