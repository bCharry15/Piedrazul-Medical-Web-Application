package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;

public interface SaveReschedulingHistoryPort {

    ReschedulingHistory save(ReschedulingHistory history);
}
