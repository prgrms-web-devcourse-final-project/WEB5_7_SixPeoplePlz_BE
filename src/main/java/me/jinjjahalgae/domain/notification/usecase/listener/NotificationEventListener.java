package me.jinjjahalgae.domain.notification.usecase.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.model.NotificationData;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationBatchEvent;
import me.jinjjahalgae.domain.notification.usecase.listener.event.NotificationEvent;
import me.jinjjahalgae.domain.notification.usecase.create.CreateNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.create.dto.NotificationCreateRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    /**
     * 재시도 관련 설정 추가
     * Exception.class, 예외가 발생하면 재시도
     * 최대 재시도 횟수 3번 (default)
     * 재시도 딜레이 1초
     *   1차 시도 실패
     *   1초 대기 -> 2차 시도 -> 실패
     *   1초 대기 -> 3차 시도 -> 실패
     *   예외 던짐
     */
    private final NotificationRetryProcessor retryProcessor;

    /**
     * NotificationEvent를 핸들링하는 리스너
     * 알림의 타입에 따라 해당하는 알림 전송
     *
     * @param event 알림 전송 이벤트
     */
    @Async
    @TransactionalEventListener
    public void handleNotificationEvent(NotificationEvent event) {
        retryProcessor.processSingle(event);
    }

    /**
     * NotificationBatchEvent를 핸들링하는 리스너
     * 여러 개의 알림을 한 번에 처리
     *
     * @param event 알림 배치 처리 이벤트
     */
    @Async
    @TransactionalEventListener
    public void handleNotificationBatchEvent(NotificationBatchEvent event) {
        retryProcessor.processBatch(event);
    }
}