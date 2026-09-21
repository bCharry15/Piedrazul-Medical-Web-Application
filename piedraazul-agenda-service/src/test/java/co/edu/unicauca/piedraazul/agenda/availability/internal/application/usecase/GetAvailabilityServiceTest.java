package co.edu.unicauca.piedraazul.agenda.availability.internal.application.usecase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.AvailabilityResponse;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.FindDoctorAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.service.AvailabilityStrategy;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

class GetAvailabilityServiceTest {

    @Mock
    private FindDoctorPort findDoctorPort;

    @Mock
    private FindDoctorAvailabilityPort findDoctorAvailabilityPort;

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    @Mock
    private AvailabilityStrategy availabilityStrategy;

    @Mock
    private Doctor doctor;

    @Mock
    private DoctorAvailability availability;

    @Mock
    private Appointment appointment;

    private GetAvailabilityService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new GetAvailabilityService(
                findDoctorPort,
                findDoctorAvailabilityPort,
                findAppointmentsPort,
                availabilityStrategy
        );
    }

    @Test
    void shouldReturnAvailabilityUsingOccupiedAppointmentTimes() {

        LocalDate date =
                LocalDate.now().plusDays(1);

        when(findDoctorPort.findById(1L))
                .thenReturn(
                        Optional.of(doctor)
                );

        when(
                findDoctorAvailabilityPort
                        .findActiveAvailability(
                                eq(doctor),
                                eq(date.getDayOfWeek())
                        )
        )
                .thenReturn(
                        Optional.of(availability)
                );

        when(
                availability.getWeekWindow()
        )
                .thenReturn(4);

        when(
                availability.getStartTime()
        )
                .thenReturn(
                        LocalTime.of(9, 0)
                );

        when(
                availability.getEndTime()
        )
                .thenReturn(
                        LocalTime.of(12, 0)
                );

        when(
                availability.getIntervalMinutes()
        )
                .thenReturn(15);

        when(
                findAppointmentsPort
                        .findByDoctorAndDate(
                                doctor,
                                date
                        )
        )
                .thenReturn(
                        List.of(appointment)
                );

        when(
                appointment.getStatus()
        )
                .thenReturn(
                        AppointmentStatus.PROGRAMADA
                );

        when(
                appointment.getTime()
        )
                .thenReturn(
                        LocalTime.of(9, 15)
                );

        when(
                availabilityStrategy
                        .calculateAvailableSlots(
                                any(),
                                any(),
                                any(),
                                any()
                        )
        )
                .thenReturn(
                        List.of(
                                LocalTime.of(9, 0),
                                LocalTime.of(9, 30)
                        )
                );

        when(
                doctor.getId()
        )
                .thenReturn(1L);

        when(
                doctor.getFullName()
        )
                .thenReturn(
                        "Jhoiner Puentes"
                );

        AvailabilityResponse response =
                service.get(
                        1L,
                        date
                );

        assertNotNull(response);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<LocalTime>>
                occupiedTimesCaptor =
                ArgumentCaptor.forClass(
                        Set.class
                );

        verify(availabilityStrategy)
                .calculateAvailableSlots(
                        eq(LocalTime.of(9, 0)),
                        eq(LocalTime.of(12, 0)),
                        eq(15),
                        occupiedTimesCaptor.capture()
                );

        Set<LocalTime> occupiedTimes =
                occupiedTimesCaptor.getValue();

        assertEquals(
                1,
                occupiedTimes.size()
        );

        assertTrue(
                occupiedTimes.contains(
                        LocalTime.of(9, 15)
                )
        );
    }

    @Test
    void shouldReleaseSlotWhenAppointmentIsCancelled() {

        LocalDate date =
                LocalDate.now().plusDays(1);

        LocalTime cancelledAppointmentTime =
                LocalTime.of(10, 30);

        when(
                findDoctorPort.findById(1L)
        )
                .thenReturn(
                        Optional.of(doctor)
                );

        when(
                findDoctorAvailabilityPort
                        .findActiveAvailability(
                                eq(doctor),
                                eq(date.getDayOfWeek())
                        )
        )
                .thenReturn(
                        Optional.of(availability)
                );

        when(
                availability.getWeekWindow()
        )
                .thenReturn(4);

        when(
                availability.getStartTime()
        )
                .thenReturn(
                        LocalTime.of(9, 0)
                );

        when(
                availability.getEndTime()
        )
                .thenReturn(
                        LocalTime.of(12, 0)
                );

        when(
                availability.getIntervalMinutes()
        )
                .thenReturn(15);

        when(
                findAppointmentsPort
                        .findByDoctorAndDate(
                                doctor,
                                date
                        )
        )
                .thenReturn(
                        List.of(appointment)
                );

        when(
                appointment.getStatus()
        )
                .thenReturn(
                        AppointmentStatus.CANCELADA
                );

        when(
                appointment.getTime()
        )
                .thenReturn(
                        cancelledAppointmentTime
                );

        when(
                availabilityStrategy
                        .calculateAvailableSlots(
                                any(),
                                any(),
                                any(),
                                any()
                        )
        )
                .thenReturn(
                        List.of(
                                LocalTime.of(9, 0),
                                LocalTime.of(9, 15),
                                LocalTime.of(9, 30),
                                LocalTime.of(9, 45),
                                LocalTime.of(10, 0),
                                LocalTime.of(10, 15),
                                LocalTime.of(10, 30),
                                LocalTime.of(10, 45),
                                LocalTime.of(11, 0),
                                LocalTime.of(11, 15),
                                LocalTime.of(11, 30),
                                LocalTime.of(11, 45)
                        )
                );

        when(
                doctor.getId()
        )
                .thenReturn(1L);

        when(
                doctor.getFullName()
        )
                .thenReturn(
                        "Laura Gomez"
                );

        AvailabilityResponse response =
                service.get(
                        1L,
                        date
                );

        assertNotNull(response);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<LocalTime>>
                occupiedTimesCaptor =
                ArgumentCaptor.forClass(
                        Set.class
                );

        verify(availabilityStrategy)
                .calculateAvailableSlots(
                        eq(LocalTime.of(9, 0)),
                        eq(LocalTime.of(12, 0)),
                        eq(15),
                        occupiedTimesCaptor.capture()
                );

        Set<LocalTime> occupiedTimes =
                occupiedTimesCaptor.getValue();

        assertTrue(
                occupiedTimes.isEmpty()
        );

        assertFalse(
                occupiedTimes.contains(
                        cancelledAppointmentTime
                )
        );
    }

    @Test
    void shouldRejectRequestWhenDoctorDoesNotExist() {

        LocalDate date =
                LocalDate.now().plusDays(1);

        when(
                findDoctorPort.findById(99L)
        )
                .thenReturn(
                        Optional.empty()
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.get(
                                        99L,
                                        date
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(
                findDoctorAvailabilityPort,
                never()
        )
                .findActiveAvailability(
                        any(),
                        any()
                );

        verify(
                availabilityStrategy,
                never()
        )
                .calculateAvailableSlots(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void shouldRejectDateOutsideSchedulingWindow() {

        LocalDate date =
                LocalDate.now()
                        .plusWeeks(2);

        when(
                findDoctorPort.findById(1L)
        )
                .thenReturn(
                        Optional.of(doctor)
                );

        when(
                findDoctorAvailabilityPort
                        .findActiveAvailability(
                                eq(doctor),
                                eq(date.getDayOfWeek())
                        )
        )
                .thenReturn(
                        Optional.of(availability)
                );

        when(
                availability.getWeekWindow()
        )
                .thenReturn(1);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.get(
                                        1L,
                                        date
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(
                findAppointmentsPort,
                never()
        )
                .findByDoctorAndDate(
                        any(),
                        any()
                );

        verify(
                availabilityStrategy,
                never()
        )
                .calculateAvailableSlots(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }
}