package co.edu.unicauca.piedraazul.agenda.availability.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.dto.ConfigurationAvailabilityRequest;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.ConfigureAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.ManageDoctorsPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

class ConfigureAvailabilityServiceTest {

    @Mock
    private ManageDoctorsPort manageDoctorsPort;

    @Mock
    private ConfigureAvailabilityPort configureAvailabilityPort;

    @Mock
    private Doctor doctor;

    private ConfigureAvailabilityService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new ConfigureAvailabilityService(
                manageDoctorsPort,
                configureAvailabilityPort);
    }

    @Test
    void shouldConfigureAvailabilitySuccessfully() {
        ConfigurationAvailabilityRequest request =
                createValidRequest();

        when(manageDoctorsPort.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(configureAvailabilityPort.save(any(DoctorAvailability.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DoctorAvailability result =
                service.configure(request);

        ArgumentCaptor<DoctorAvailability> availabilityCaptor =
                ArgumentCaptor.forClass(DoctorAvailability.class);

        verify(configureAvailabilityPort)
                .save(availabilityCaptor.capture());

        DoctorAvailability savedAvailability =
                availabilityCaptor.getValue();

        assertSame(
                doctor,
                savedAvailability.getDoctor());

        assertEquals(
                DayOfWeek.MONDAY,
                savedAvailability.getDayOfWeek());

        assertEquals(
                LocalTime.of(9, 0),
                savedAvailability.getStartTime());

        assertEquals(
                LocalTime.of(12, 0),
                savedAvailability.getEndTime());

        assertEquals(
                15,
                savedAvailability.getIntervalMinutes());

        assertEquals(
                4,
                savedAvailability.getWeekWindow());

        assertEquals(
                true,
                savedAvailability.getActive());

        assertSame(
                savedAvailability,
                result);
    }

    @Test
    void shouldRejectInvalidTimeRange() {
        ConfigurationAvailabilityRequest request =
                createValidRequest();

        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(9, 0));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.configure(request));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verify(
                manageDoctorsPort,
                never())
                .findById(any());

        verify(
                configureAvailabilityPort,
                never())
                .save(any());
    }

    @Test
    void shouldRejectInvalidIntervalMinutes() {
        ConfigurationAvailabilityRequest request =
                createValidRequest();

        request.setIntervalMinutes(0);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.configure(request));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verify(
                configureAvailabilityPort,
                never())
                .save(any());
    }

    @Test
    void shouldUpdateExistingAvailability() {
        Long availabilityId = 10L;

        ConfigurationAvailabilityRequest request =
                createValidRequest();

        DoctorAvailability existingAvailability =
                new DoctorAvailability();

        when(configureAvailabilityPort.findById(availabilityId))
                .thenReturn(Optional.of(existingAvailability));

        when(manageDoctorsPort.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(configureAvailabilityPort.save(existingAvailability))
                .thenReturn(existingAvailability);

        DoctorAvailability result =
                service.update(
                        availabilityId,
                        request);

        assertSame(
                existingAvailability,
                result);

        assertSame(
                doctor,
                existingAvailability.getDoctor());

        assertEquals(
                DayOfWeek.MONDAY,
                existingAvailability.getDayOfWeek());

        assertEquals(
                LocalTime.of(9, 0),
                existingAvailability.getStartTime());

        assertEquals(
                LocalTime.of(12, 0),
                existingAvailability.getEndTime());

        assertEquals(
                15,
                existingAvailability.getIntervalMinutes());

        assertEquals(
                4,
                existingAvailability.getWeekWindow());

        assertEquals(
                true,
                existingAvailability.getActive());

        verify(configureAvailabilityPort)
                .save(existingAvailability);
    }

    @Test
    void shouldListActiveAvailabilityByDoctor() {
        DoctorAvailability firstAvailability =
                new DoctorAvailability();

        DoctorAvailability secondAvailability =
                new DoctorAvailability();

        when(manageDoctorsPort.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(configureAvailabilityPort.findByDoctorAndActive(doctor))
                .thenReturn(List.of(
                        firstAvailability,
                        secondAvailability));

        List<DoctorAvailability> result =
                service.listByDoctor(1L);

        assertEquals(
                2,
                result.size());

        verify(configureAvailabilityPort)
                .findByDoctorAndActive(doctor);
    }

    private ConfigurationAvailabilityRequest createValidRequest() {
        ConfigurationAvailabilityRequest request =
                new ConfigurationAvailabilityRequest();

        request.setDoctorId(1L);
        request.setDayOfWeek(DayOfWeek.MONDAY);
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setIntervalMinutes(15);
        request.setWeekWindow(4);

        return request;
    }
}