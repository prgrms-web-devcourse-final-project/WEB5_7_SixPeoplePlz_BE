package me.jinjjahalgae.global.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    /**
     * 단일 기기로 알림을 전송
     *
     * @param token 대상 기기의 FCM 토큰
     * @param title 알림 제목
     * @param body  알림 내용
     */
    @Override
    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isBlank()) {
            log.warn("FCM 토큰이 비어있어 알림을 전송할 수 없습니다.");

            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 전송 성공. Token: {}", token);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 전송 실패. Token: {}", token, e);
        }
    }

    /**
     * 여러 기기로 동일한 알림을 전송
     *
     * @param tokens 대상 기기들의 FCM 토큰 목록
     * @param title  알림 제목
     * @param body   알림 내용
     */
    @Override
    public void sendMulticastNotification(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("FCM 토큰 목록이 비어있어 알림을 전송할 수 없습니다.");

            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.error("FCM 알림 전송 실패. Token: {}, Error: {}", tokens.get(i), responses.get(i).getException());
                    }
                }
            }
            log.info("FCM 멀티캐스트 알림 전송 완료. 성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 알림 전송 중 예외 발생.", e);
        }
    }
}
