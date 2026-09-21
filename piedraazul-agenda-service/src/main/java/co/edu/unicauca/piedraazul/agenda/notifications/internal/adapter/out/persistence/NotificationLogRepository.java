package co.edu.unicauca.piedraazul.agenda.notifications.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.piedraazul.agenda.notifications.internal.domain.model.NotificationLog;

public interface NotificationLogRepository
        extends JpaRepository<NotificationLog, Long> {
}
