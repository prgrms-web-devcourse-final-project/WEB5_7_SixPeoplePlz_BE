package me.jinjjahalgae.domain.notification.usecase.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final CreateNotificationUseCase createNotificationUseCase;

    /**
     * NotificationEvent를 핸들링하는 리스너
     * 알림의 타입에 따라 해당하는 알림 전송
     *
     * @param event 알림 전송 이벤트
     */
    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            createNotificationUseCase.execute(
                    new NotificationCreateRequest(
                            event.notificationType().name(),
                            event.contractId(),
                            event.actorUserId()
                    )
            );
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}