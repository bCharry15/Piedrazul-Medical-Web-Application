package co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.in.web;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.in.ManageDoctorsUseCase;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.dto.DoctorRequest;

@RestController
public class DoctorRestController {

    private final ManageDoctorsUseCase manageDoctorsUseCase;

    public DoctorRestController(ManageDoctorsUseCase manageDoctorsUseCase) {
        this.manageDoctorsUseCase = manageDoctorsUseCase;
    }

    @GetMapping("/api/doctors")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> listAll() {
        return manageDoctorsUseCase.listAll()
                .stream()
                .map(this::convertDoctorToResponse)
                .toList();
    }

    @GetMapping("/api/doctors/{doctorId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getById(@PathVariable Long doctorId) {
        return convertDoctorToResponse(
                manageDoctorsUseCase.getById(doctorId)
        );
    }

    @PostMapping("/api/doctors")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createDoctor(@RequestBody DoctorRequest request) {
        return convertDoctorToResponse(
                manageDoctorsUseCase.createDoctor(request)
        );
    }

    @PutMapping("/api/doctors/{doctorId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> updateDoctor(@PathVariable Long doctorId,
                                                @RequestBody DoctorRequest request) {
        return convertDoctorToResponse(
                manageDoctorsUseCase.updateDoctor(doctorId, request)
        );
    }

    @DeleteMapping("/api/doctors/{doctorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable Long doctorId) {
        manageDoctorsUseCase.deleteDoctor(doctorId);
    }

    private Map<String, Object> convertDoctorToResponse(Doctor doctor) {
        Map<String, Object> response = new HashMap<>();

        response.put("id", doctor.getId());
        response.put("nombreCompleto", doctor.getFullName());
        response.put("especialidad", doctor.getSpecialty());
        response.put("intervalMinutes", doctor.getIntervalMinutes());
        response.put("active", doctor.getActive());

        User user = doctor.getUser();

        if (user != null) {
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("userRole", user.getRole() != null ? user.getRole().name() : "");
            response.put("userStatus", user.getStatus() != null ? user.getStatus().name() : "");
        } else {
            response.put("userId", null);
            response.put("username", "");
            response.put("userRole", "");
            response.put("userStatus", "");
        }

        return response;
    }
}
