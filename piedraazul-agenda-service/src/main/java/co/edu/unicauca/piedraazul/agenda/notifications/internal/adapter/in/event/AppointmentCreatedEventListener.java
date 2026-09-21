package co.edu.unicauca.piedraazul.agenda.notifications.internal.adapter.in.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.appointments.api.event.AppointmentCreatedEvent;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.service.NotificationService;

@Component
public class AppointmentCreatedEventListener {

    private final NotificationService notificationService;

    public AppointmentCreatedEventListener(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handle(AppointmentCreatedEvent event) {
        notificationService.processAppointmentCreated(event);
    }
}