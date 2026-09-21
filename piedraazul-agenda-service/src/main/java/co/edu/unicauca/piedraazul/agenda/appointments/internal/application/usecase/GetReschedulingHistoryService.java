package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in.GetReschedulingHistoryUseCase;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindReschedulingHistoryPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;

@Service
public class GetReschedulingHistoryService implements GetReschedulingHistoryUseCase {

    private final FindAppointmentsPort findAppointmentsPort;
    private final FindReschedulingHistoryPort findReschedulingHistoryPort;

    public GetReschedulingHistoryService(FindAppointmentsPort findAppointmentsPort,
                                                   FindReschedulingHistoryPort findReschedulingHistoryPort) {
        this.findAppointmentsPort = findAppointmentsPort;
        this.findReschedulingHistoryPort = findReschedulingHistoryPort;
    }

    @Override
    public List<Map<String, Object>> getByAppointmentId(Long appointmentId) {
        if (appointmentId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id de la cita es obligatorio."
            );
        }

        findAppointmentsPort.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una cita con id: " + appointmentId
                ));

        return findReschedulingHistoryPort.findByAppointmentId(appointmentId)
                .stream()
                .map(this::convertHistoryToMap)
                .toList();
    }

    private Map<String, Object> convertHistoryToMap(ReschedulingHistory history) {
        Map<String, Object> response = new HashMap<>();

        response.put("id", history.getId());
        response.put("appointmentId", history.getAppointment().getId());
        response.put("dateAnterior", history.getPreviousDate());
        response.put("timeAnterior", history.getPreviousTime());
        response.put("dateNueva", history.getNewDate());
        response.put("timeNueva", history.getNewTime());
        response.put("responsable", history.getResponsible());
        response.put("motivo", history.getReason());
        response.put("dateCambio", history.getChangeDate());

        return response;
    }
}
