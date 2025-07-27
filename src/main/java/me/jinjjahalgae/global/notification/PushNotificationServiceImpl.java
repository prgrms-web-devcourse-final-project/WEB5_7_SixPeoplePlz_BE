package me.jinjjahalgae.global.notification;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {
    private final WebClient webClient;

    /**
     * 단일 기기로 알림을 전송
     *
     * @param token 대상 기기의 FCM 토큰
     * @param title 알림 제목
     * @param body  알림 내용
     */
    @Override
    public void sendNotification(String token, String title, String body) {
        sendMulticastNotification(List.of(token), title, body);
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
            log.warn("Expo 푸시 토큰 목록이 비어있어 알림을 전송할 수 없습니다.");

            return;
        }

        log.info("Expo 푸시 알림 발송 요청. 대상 토큰 수: {}", tokens.size());

        ExpoPushRequest expoRequest = new ExpoPushRequest(tokens, title, body);

        // WebClient로 Expo 서버에 비동기 POST 요청
        webClient.post()
                .uri("https://exp.host/--/api/v2/push/send")
                .body(Mono.just(expoRequest), ExpoPushRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Expo 푸시 알림 발송 요청 성공. Response: {}", response))
                .doOnError(error -> log.error("Expo 푸시 알림 발송 요청 실패.", error))
                .subscribe();

    }

    private record ExpoPushRequest(List<String> to, String title, String body) {}
}
