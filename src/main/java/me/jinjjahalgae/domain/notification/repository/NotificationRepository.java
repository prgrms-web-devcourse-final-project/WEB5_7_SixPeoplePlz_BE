package me.jinjjahalgae.domain.notification.repository;

import me.jinjjahalgae.domain.notification.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUserId(Long userId, Pageable pageable);

    // 알림 id로 찾아 삭제
    void deleteById(Long id);

    // userid에 해당하는 모든 알림 삭제. 삭제된 개수 반환
    Long deleteAllByUserId(Long userId);

    // userid에 해당하는 모든 알림 중 안 읽은 알림의 개수 반환
    @Query("""
            SELECT COUNT(n) FROM Notification n 
                        WHERE n.userId = :userId AND n.read = false 
    """)
    Long countUnreadNotificationByUserId(@Param("userId") Long userId);
}
