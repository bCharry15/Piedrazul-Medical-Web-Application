package co.edu.unicauca.piedraazul.agenda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.jayway.jsonpath.JsonPath;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase.CreateAppointmentService;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence.DoctorAvailabilityRepository;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.SynchronizeUsersKeycloakService;

/**
 * Prueba de integración de la agenda: recorre la API REST, la seguridad
 * y la persistencia con una base de datos H2 en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:agenda_it;MODE=MariaDB;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "notification.mail.enabled=false"
})
class AgendaIntegrationTest {

    private static final LocalTime APPOINTMENT_TIME = LocalTime.of(8, 40);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @MockBean
    private SynchronizeUsersKeycloakService synchronizeUsersKeycloakService;

    @Test
    void shouldExposeOnlyPublicHealthSummary() throws Exception {
        mockMvc.perform(get("/api/agenda/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test
    void shouldRequireAdminForHealthMetricsAndLogs() throws Exception {
        for (String path : new String[]{"health", "metrics", "logfile"}) {
            mockMvc.perform(get("/api/monitoring/" + path))
                    .andExpect(status().isUnauthorized());
            for (String role : new String[]{"PATIENT", "DOCTOR", "SCHEDULER"}) {
                mockMvc.perform(get("/api/monitoring/" + path)
                                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + role))))
                        .andExpect(status().isForbidden());
            }
        }
    }

    @Test
    void shouldAllowAdminToReadDatabaseHealthAndUptime() throws Exception {
        var admin = jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
        mockMvc.perform(get("/api/monitoring/health").with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.db.status").value("UP"));
        mockMvc.perform(get("/api/monitoring/metrics/process.uptime").with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].value").isNumber());
        mockMvc.perform(get("/api/monitoring/logfile").with(admin).header("Range", "bytes=-1024"))
                .andExpect(status().isPartialContent());
        mockMvc.perform(post("/api/monitoring/health").with(admin))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Rechaza la consulta de la agenda sin token")
    void shouldRejectAgendaRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/appointments")
                        .param("doctorId", "1")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Crea una cita por la API, la persiste y la muestra en la agenda del médico")
    void shouldCreateAppointmentPersistItAndListItInDoctorAgenda() throws Exception {
        LocalDate date = nextWorkingDate();
        Doctor doctor = createDoctorWithAvailability(date);

        MvcResult result = mockMvc.perform(post("/api/appointments")
                        .with(scheduler())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson("9900112233", "Laura", doctor.getId(), date)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROGRAMADA"))
                .andExpect(jsonPath("$.numeroDocument").value("9900112233"))
                .andReturn();

        Number createdId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Long appointmentId = createdId.longValue();

        Appointment stored = appointmentRepository.findById(appointmentId).orElseThrow();
        assertThat(stored.getDoctor().getId()).isEqualTo(doctor.getId());
        assertThat(stored.getDate()).isEqualTo(date);
        assertThat(stored.getTime()).isEqualTo(APPOINTMENT_TIME);
        assertThat(stored.getStatus()).isEqualTo(AppointmentStatus.PROGRAMADA);
        assertThat(stored.getPatient().getDocumentNumber()).isEqualTo("9900112233");

        mockMvc.perform(get("/api/appointments")
                        .with(scheduler())
                        .param("doctorId", doctor.getId().toString())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(1))
                .andExpect(jsonPath("$.appointments[0].id").value(appointmentId.intValue()))
                .andExpect(jsonPath("$.appointments[0].status").value("PROGRAMADA"));

        mockMvc.perform(post("/api/appointments")
                        .with(scheduler())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson("9900445566", "Andres", doctor.getId(), date)))
                .andExpect(status().isConflict());

        assertThat(appointmentRepository.findByDoctorAndDateOrderByTimeAsc(doctor, date)).hasSize(1);
    }

    private RequestPostProcessor scheduler() {
        return jwt()
                .jwt(token -> token.claim("preferred_username", "scheduler"))
                .authorities(new SimpleGrantedAuthority("ROLE_SCHEDULER"));
    }

    @SuppressWarnings("unchecked")
    private LocalDate nextWorkingDate() {
        Set<LocalDate> holidays = (Set<LocalDate>) ReflectionTestUtils.getField(
                CreateAppointmentService.class, "FESTIVOS_COLOMBIA_2026");

        LocalDate date = LocalDate.now().plusDays(7);

        while (holidays != null && holidays.contains(date)) {
            date = date.plusDays(1);
        }

        return date;
    }

    private Doctor createDoctorWithAvailability(LocalDate date) {
        Doctor doctor = new Doctor();
        doctor.setFullName("Doctora Integracion");
        doctor.setSpecialty("Consulta General");
        doctor.setIntervalMinutes(20);
        Doctor savedDoctor = doctorRepository.save(doctor);

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(savedDoctor);
        availability.setDayOfWeek(date.getDayOfWeek());
        availability.setStartTime(LocalTime.of(8, 0));
        availability.setEndTime(LocalTime.of(12, 0));
        availability.setIntervalMinutes(20);
        availability.setWeekWindow(4);
        availability.setActive(true);
        doctorAvailabilityRepository.save(availability);

        return savedDoctor;
    }

    private String appointmentJson(String documentNumber, String firstNames, Long doctorId, LocalDate date) {
        return """
                {
                  "numeroDocument": "%s",
                  "tipoDocument": "CC",
                  "nombres": "%s",
                  "apellidos": "Prueba Integracion",
                  "celular": "3001112233",
                  "gender": "MUJER",
                  "dateNacimiento": "1995-05-20",
                  "correo": "integracion@piedraazul.test",
                  "doctorId": %d,
                  "date": "%s",
                  "time": "%s",
                  "notes": "Cita creada por la prueba de integracion"
                }
                """.formatted(documentNumber, firstNames, doctorId, date, APPOINTMENT_TIME);
    }
}
