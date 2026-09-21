package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.service;
import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

@Service
public class AppointmentStatusStateService {

    public AppointmentStatus getSafeStatus(AppointmentStatus statusAppointment) {
        if (statusAppointment == null) {
            return AppointmentStatus.PROGRAMADA;
        }

        return statusAppointment;
    }

    public boolean isActiveStatus(AppointmentStatus statusAppointment) {
        if (statusAppointment == null) {
            return false;
        }

        return statusAppointment == AppointmentStatus.PROGRAMADA
                || statusAppointment == AppointmentStatus.CONFIRMADA
                || statusAppointment == AppointmentStatus.PENDIENTE;
    }

    public boolean isFinalStatus(AppointmentStatus statusAppointment) {
        if (statusAppointment == null) {
            return false;
        }

        return statusAppointment == AppointmentStatus.ATENDIDA
                || statusAppointment == AppointmentStatus.COMPLETADA
                || statusAppointment == AppointmentStatus.CANCELADA
                || statusAppointment == AppointmentStatus.NO_VINO;
    }
}
