package co.edu.unicauca.piedraazul.agenda.doctors.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;
import co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence.DoctorAvailabilityRepository;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.dto.DoctorRequest;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.ManageDoctorsPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.SynchronizeUsersKeycloakService;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.EncodePasswordPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.ManageUsersPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;

@ExtendWith(MockitoExtension.class)
class ManageDoctorsServiceTest {

    @Mock
    private ManageDoctorsPort manageDoctorsPort;

    @Mock
    private ManageUsersPort manageUsersPort;

    @Mock
    private EncodePasswordPort encodePasswordPort;

    @Mock
    private SynchronizeUsersKeycloakService synchronizeUsersKeycloakService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    private ManageDoctorsService service;

    @BeforeEach
    void setUp() {

        service =
                new ManageDoctorsService(
                        manageDoctorsPort,
                        manageUsersPort,
                        encodePasswordPort,
                        synchronizeUsersKeycloakService,
                        doctorRepository,
                        appointmentRepository,
                        doctorAvailabilityRepository
                );
    }

    @Test
    void shouldListAllDoctors() {

        Doctor doctor1 =
                new Doctor();

        Doctor doctor2 =
                new Doctor();

        List<Doctor> doctors =
                List.of(
                        doctor1,
                        doctor2
                );

        when(
                manageDoctorsPort.listAll()
        ).thenReturn(
                doctors
        );

        List<Doctor> result =
                service.listAll();

        assertNotNull(
                result
        );

        assertEquals(
                2,
                result.size()
        );

        assertSame(
                doctors,
                result
        );

        verify(
                manageDoctorsPort
        ).listAll();
    }

    @Test
    void shouldRejectNullCreateRequest() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.createDoctor(
                                        null
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                manageDoctorsPort,
                manageUsersPort,
                encodePasswordPort,
                synchronizeUsersKeycloakService
        );
    }

    @Test
    void shouldRejectCreateRequestWithBlankName() {

        DoctorRequest request =
                buildRequest(
                        "   ",
                        "Medicina General",
                        30,
                        "doctor1",
                        "Doctor123"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.createDoctor(
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                manageUsersPort
        );
    }

    @Test
    void shouldRejectCreateRequestWithInvalidInterval() {

        DoctorRequest request =
                buildRequest(
                        "Carlos Perez",
                        "Medicina General",
                        0,
                        "doctor1",
                        "Doctor123"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.createDoctor(
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                manageUsersPort
        );
    }

    @Test
    void shouldRejectShortPassword() {

        DoctorRequest request =
                buildRequest(
                        "Carlos Perez",
                        "Medicina General",
                        30,
                        "doctor1",
                        "123"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.createDoctor(
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                manageUsersPort
        );
    }

    @Test
    void shouldRejectDuplicateUsername() {

        DoctorRequest request =
                buildRequest(
                        "Carlos Perez",
                        "Medicina General",
                        30,
                        "doctor1",
                        "Doctor123"
                );

        when(
                manageUsersPort.findByUsername(
                        "doctor1"
                )
        ).thenReturn(
                Optional.of(
                        new User()
                )
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.createDoctor(
                                        request
                                )
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(
                synchronizeUsersKeycloakService,
                never()
        ).synchronizeUserRequired(
                any(),
                any(),
                any()
        );

        verify(
                manageDoctorsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldCreateDoctorSuccessfully() {

        DoctorRequest request =
                buildRequest(
                        "  Carlos Perez  ",
                        "  Medicina General  ",
                        30,
                        "  doctor1  ",
                        "Doctor123"
                );

        when(
                manageUsersPort.findByUsername(
                        "doctor1"
                )
        ).thenReturn(
                Optional.empty()
        );

        when(
                encodePasswordPort.encode(
                        "Doctor123"
                )
        ).thenReturn(
                "password-codificado"
        );

        when(
                manageUsersPort.save(
                        any(
                                User.class
                        )
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(
                                0
                        )
        );

        when(
                manageDoctorsPort.save(
                        any(
                                Doctor.class
                        )
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(
                                0
                        )
        );

        Doctor result =
                service.createDoctor(
                        request
                );

        assertNotNull(
                result
        );

        assertEquals(
                "Carlos Perez",
                result.getFullName()
        );

        assertEquals(
                "Medicina General",
                result.getSpecialty()
        );

        assertEquals(
                30,
                result.getIntervalMinutes()
        );

        assertNotNull(
                result.getUser()
        );

        assertEquals(
                "doctor1",
                result.getUser()
                        .getUsername()
        );

        assertEquals(
                "password-codificado",
                result.getUser()
                        .getPassword()
        );

        assertEquals(
                UserRole.DOCTOR,
                result.getUser()
                        .getRole()
        );

        assertEquals(
                UserStatus.ACTIVE,
                result.getUser()
                        .getStatus()
        );

        verify(
                synchronizeUsersKeycloakService
        ).synchronizeUserRequired(
                "doctor1",
                "Doctor123",
                UserRole.DOCTOR
        );

        verify(
                encodePasswordPort
        ).encode(
                "Doctor123"
        );

        verify(
                manageDoctorsPort
        ).save(
                any(
                        Doctor.class
                )
        );
    }

    @Test
    void shouldRejectNullDoctorIdWhenGetting() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.getById(
                                        null
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                manageDoctorsPort
        );
    }

    @Test
    void shouldReturnNotFoundWhenDoctorDoesNotExist() {

        when(
                manageDoctorsPort.findById(
                        99L
                )
        ).thenReturn(
                Optional.empty()
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.getById(
                                        99L
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(
                manageDoctorsPort
        ).findById(
                99L
        );
    }

    @Test
    void shouldReturnDoctorById() {

        Doctor doctor =
                new Doctor();

        doctor.setFullName(
                "Carlos Perez"
        );

        when(
                manageDoctorsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        doctor
                )
        );

        Doctor result =
                service.getById(
                        1L
                );

        assertSame(
                doctor,
                result
        );

        assertEquals(
                "Carlos Perez",
                result.getFullName()
        );
    }

    @Test
    void shouldRejectNullDoctorIdWhenUpdating() {

        DoctorRequest request =
                buildRequest(
                        "Carlos Perez",
                        "Medicina General",
                        30,
                        null,
                        null
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.updateDoctor(
                                        null,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                doctorRepository
        );
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingDoctor() {

        DoctorRequest request =
                buildRequest(
                        "Carlos Perez",
                        "Medicina General",
                        30,
                        null,
                        null
                );

        when(
                doctorRepository.findByIdAndActiveTrue(
                        99L
                )
        ).thenReturn(
                Optional.empty()
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.updateDoctor(
                                        99L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(
                doctorRepository,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldUpdateDoctorSuccessfully() {

        Doctor doctor =
                new Doctor();

        doctor.setFullName(
                "Nombre Antiguo"
        );

        doctor.setSpecialty(
                "Especialidad Antigua"
        );

        doctor.setIntervalMinutes(
                15
        );

        DoctorRequest request =
                buildRequest(
                        "  Nuevo Nombre  ",
                        "  Nueva Especialidad  ",
                        45,
                        null,
                        null
                );

        when(
                doctorRepository.findByIdAndActiveTrue(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        doctor
                )
        );

        when(
                doctorRepository.save(
                        doctor
                )
        ).thenReturn(
                doctor
        );

        Doctor result =
                service.updateDoctor(
                        1L,
                        request
                );

        assertSame(
                doctor,
                result
        );

        assertEquals(
                "Nuevo Nombre",
                result.getFullName()
        );

        assertEquals(
                "Nueva Especialidad",
                result.getSpecialty()
        );

        assertEquals(
                45,
                result.getIntervalMinutes()
        );

        verify(
                doctorRepository
        ).save(
                doctor
        );
    }

    @Test
    void shouldRejectNullDoctorIdWhenDeleting() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.deleteDoctor(
                                        null
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                doctorRepository,
                appointmentRepository,
                doctorAvailabilityRepository
        );
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingDoctor() {

        when(
                doctorRepository.findByIdAndActiveTrue(
                        99L
                )
        ).thenReturn(
                Optional.empty()
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.deleteDoctor(
                                        99L
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                appointmentRepository,
                doctorAvailabilityRepository
        );
    }

    @Test
    void shouldSoftDeleteDoctorUserAndAvailabilities() {

        User user =
                new User();

        user.setUsername(
                "doctor1"
        );

        user.setStatus(
                UserStatus.ACTIVE
        );


        Doctor doctor =
                new Doctor();

        doctor.setFullName(
                "Carlos Perez"
        );

        doctor.setActive(
                true
        );

        doctor.setUser(
                user
        );


        DoctorAvailability availability1 =
                new DoctorAvailability();

        availability1.setActive(
                true
        );


        DoctorAvailability availability2 =
                new DoctorAvailability();

        availability2.setActive(
                true
        );


        List<DoctorAvailability> availabilities =
                List.of(
                        availability1,
                        availability2
                );


        when(
                doctorRepository.findByIdAndActiveTrue(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        doctor
                )
        );

        when(
                appointmentRepository.countByDoctorId(
                        1L
                )
        ).thenReturn(
                3L
        );

        when(
                doctorAvailabilityRepository.findByDoctorId(
                        1L
                )
        ).thenReturn(
                availabilities
        );


        service.deleteDoctor(
                1L
        );


        assertEquals(
                UserStatus.INACTIVE,
                user.getStatus()
        );

        assertEquals(
                false,
                availability1.getActive()
        );

        assertEquals(
                false,
                availability2.getActive()
        );


        verify(
                manageUsersPort
        ).save(
                user
        );

        verify(
                doctorAvailabilityRepository
        ).saveAll(
                availabilities
        );

        verify(
                doctorRepository
        ).save(
                doctor
        );

        verify(
                synchronizeUsersKeycloakService
        ).disableUserRequired(
                "doctor1"
        );

        verify(
                appointmentRepository
        ).countByDoctorId(
                1L
        );
    }

    @Test
    void shouldDeleteDoctorWithoutUserWithoutCallingKeycloak() {

        Doctor doctor =
                new Doctor();

        doctor.setFullName(
                "Carlos Perez"
        );

        doctor.setActive(
                true
        );

        doctor.setUser(
                null
        );


        when(
                doctorRepository.findByIdAndActiveTrue(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        doctor
                )
        );

        when(
                appointmentRepository.countByDoctorId(
                        1L
                )
        ).thenReturn(
                0L
        );

        when(
                doctorAvailabilityRepository.findByDoctorId(
                        1L
                )
        ).thenReturn(
                List.of()
        );


        service.deleteDoctor(
                1L
        );


        verify(
                manageUsersPort,
                never()
        ).save(
                any()
        );

        verify(
                synchronizeUsersKeycloakService,
                never()
        ).disableUserRequired(
                any()
        );

        verify(
                doctorAvailabilityRepository
        ).saveAll(
                List.of()
        );

        verify(
                doctorRepository
        ).save(
                doctor
        );
    }

    private DoctorRequest buildRequest(
            String fullName,
            String specialty,
            Integer intervalMinutes,
            String username,
            String password
    ) {

        DoctorRequest request =
                new DoctorRequest();

        request.setFullName(
                fullName
        );

        request.setSpecialty(
                specialty
        );

        request.setIntervalMinutes(
                intervalMinutes
        );

        request.setUsername(
                username
        );

        request.setPassword(
                password
        );

        return request;
    }
}