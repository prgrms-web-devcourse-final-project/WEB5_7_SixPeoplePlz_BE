package me.jinjjahalgae.domain.notification.repository;

import me.jinjjahalgae.domain.notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
