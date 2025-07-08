package me.jinjjahalgae.domain.notification.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.entities.Notification;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.domain.notification.usecase.interfaces.MarkSingleNotificationAsReadUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkSingleNotificationAsReadUseCaseImpl implements MarkSingleNotificationAsReadUseCase {

    private final NotificationRepository notificationRepository;

    @Override
    public Long execute(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ErrorCode.NOTIFICATION_NOT_FOUND.serviceException("존재하지 않는 알림 id입니다."));

        notification.read();

        return notification.getId();
    }
}
