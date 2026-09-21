package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence;
import java.util.List;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindReschedulingHistoryPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.SaveReschedulingHistoryPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.ReschedulingHistoryRepository;

@Component
public class ReschedulingHistoryPersistenceAdapter implements SaveReschedulingHistoryPort,
        FindReschedulingHistoryPort {

    private final ReschedulingHistoryRepository historyReschedulingRepository;

    public ReschedulingHistoryPersistenceAdapter(
            ReschedulingHistoryRepository historyReschedulingRepository) {
        this.historyReschedulingRepository = historyReschedulingRepository;
    }

    @Override
    public ReschedulingHistory save(ReschedulingHistory history) {
        return historyReschedulingRepository.save(history);
    }

    @Override
    public List<ReschedulingHistory> findByAppointmentId(Long appointmentId) {
        return historyReschedulingRepository.findByAppointmentIdOrderByChangeDateDesc(appointmentId);
    }
}
