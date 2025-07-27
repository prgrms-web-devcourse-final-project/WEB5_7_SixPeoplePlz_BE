package me.jinjjahalgae.domain.notification.usecase.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.entity.FcmToken;
import me.jinjjahalgae.domain.auth.repository.FcmTokenRepository;
import me.jinjjahalgae.domain.notification.usecase.listener.event.PushNotificationSendEvent;
import me.jinjjahalgae.global.notification.PushNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationEventListener {
    private final PushNotificationService pushNotificationService;
    private final FcmTokenRepository fcmTokenRepository;

    // PushNotificationSendEvent를 핸들링하여 FCM 푸시 알림을 전송
    @Async
    @TransactionalEventListener
    public void handlePushNotificationSendEvent(PushNotificationSendEvent event) {
        log.info("FCM 푸시 알림 전송 시작. 대상자 수: {}", event.targetUserIds().size());

        List<Long> targetUserIds = event.targetUserIds();

        if (targetUserIds == null || targetUserIds.isEmpty()) {
            return;
        }

        // 모든 대상 유저의 모든 FCM 토큰을 한 번에 조회
        List<String> allTokens = fcmTokenRepository.findAllByUserIdIn(targetUserIds)
                .stream()
                .map(FcmToken::getToken)
                .toList();

        if (allTokens.isEmpty()) {
            log.warn("대상자들의 FCM 토큰이 존재하지 않아 알림을 전송할 수 없습니다.");

            return;
        }

        // 멀티캐스트 메시지로 한 번에 전송
        pushNotificationService.sendMulticastNotification(allTokens, event.title(), event.body());
    }
}
