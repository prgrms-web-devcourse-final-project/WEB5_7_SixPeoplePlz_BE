package me.jinjjahalgae.domain.notification.usecase.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.model.NotificationData;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationBatchEvent;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRetryProcessor {

    private final CreateNotificationUseCase createNotificationUseCase;

    /**
     * 단일 알림 재시도 처리
     */
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void processSingle(NotificationEvent event) {
        createNotificationUseCase.execute(
                new NotificationCreateRequest(
                        event.notificationType(),
                        event.contractId(),
                        event.actorUserId()
                )
        );
    }

    /**
     * 배치 알림 재시도 처리
     */
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void processBatch(NotificationBatchEvent event) {
        for (NotificationData data : event.notificationData()) {
            createNotificationUseCase.execute(
                    new NotificationCreateRequest(
                            event.notificationType(),
                            data.contractId(),
                            data.actorUserId()
                    )
            );
        }
    }

    @Recover
    public void recover(Exception e, NotificationBatchEvent event) {
        log.error("배치 알림 재시도 실패: {}", event, e);
        // TODO: 실패 이벤트 재처리 로직
    }

    @Recover
    public void recover(Exception e, NotificationEvent event) {
        log.error("단일 알림 재시도 실패 - 유저ID: {}, 계약ID: {}", event.actorUserId(), event.contractId(), e);
        // TODO: 실패 이벤트 재처리 로직
    }
}
