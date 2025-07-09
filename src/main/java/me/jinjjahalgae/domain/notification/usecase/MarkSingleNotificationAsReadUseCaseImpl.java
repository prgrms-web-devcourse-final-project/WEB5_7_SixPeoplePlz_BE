package me.jinjjahalgae.domain.notification.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.entities.Notification;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.domain.notification.usecase.interfaces.MarkSingleNotificationAsReadUseCase;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkSingleNotificationAsReadUseCaseImpl implements MarkSingleNotificationAsReadUseCase {

    private final NotificationRepository notificationRepository;

    /**
     *  개별 알림 읽음 처리
     *  알림id를 받고, 상태변경된 알림id를 반환한다
     *
     * @param notificationId 읽음처리할 알림 id
     * @return 읽음처리된 알림 id
     */
    @Override
    @Transactional
    public Long execute(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ErrorCode.NOTIFICATION_NOT_FOUND.serviceException("존재하지 않는 알림 id입니다 : " + notificationId));

        notification.markRead();

        return notification.getId();
    }
}
