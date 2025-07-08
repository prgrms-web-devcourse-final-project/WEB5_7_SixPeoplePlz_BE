package me.jinjjahalgae.domain.auth.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.dto.social.KakaoPayload;
import me.jinjjahalgae.domain.auth.dto.social.SocialProfile;
import me.jinjjahalgae.domain.auth.enums.Provider;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import me.jinjjahalgae.global.exception.AppException;

@Slf4j
@Component
public class KakaoLogin implements SocialLogin {
    private final WebClient webClient;

    public KakaoLogin(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://kapi.kakao.com") // 기본 URL 설정
                .build();
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public SocialProfile getUserInfo(String accessToken) {
        try {
            KakaoPayload payload = webClient.get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoPayload.class)
                    .block();

            if (payload == null) {
                throw ErrorCode.INVALID_TOKEN.serviceException("카카오 사용자 정보 조회 실패");
            }

            return new SocialProfile(
                    String.valueOf(payload.id()),
                    payload.kakaoAccount().email(),
                    payload.kakaoAccount().profile().nickname(),
                    payload.kakaoAccount().profile().nickname()
            );
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ErrorCode.INVALID_TOKEN.serviceException("카카오 인증 중 예외 발생: " + e.getMessage());
        }
    }
}
