package co.edu.unicauca.piedraazul.agenda.notifications.internal.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out.NotificationLogPort;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.domain.model.NotificationLog;

@Component
public class NotificationLogPersistenceAdapter implements NotificationLogPort {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationLogPersistenceAdapter(
            NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @Override
    public NotificationLog save(NotificationLog notificationLog) {
        return notificationLogRepository.save(notificationLog);
    }

    @Override
    public List<NotificationLog> findAll() {
        return notificationLogRepository.findAll();
    }
}