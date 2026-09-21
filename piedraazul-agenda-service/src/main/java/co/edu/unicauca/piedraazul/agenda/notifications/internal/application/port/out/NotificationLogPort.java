package co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out;

import java.util.List;

import co.edu.unicauca.piedraazul.agenda.notifications.internal.domain.model.NotificationLog;

public interface NotificationLogPort {

    NotificationLog save(NotificationLog notificationLog);

    List<NotificationLog> findAll();
}