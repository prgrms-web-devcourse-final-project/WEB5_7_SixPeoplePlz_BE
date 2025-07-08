package me.jinjjahalgae.domain.auth.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.dto.social.NaverPayload;
import me.jinjjahalgae.domain.auth.dto.social.SocialProfile;
import me.jinjjahalgae.domain.auth.enums.Provider;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import me.jinjjahalgae.global.exception.AppException;

@Slf4j
@Component
public class NaverLogin implements SocialLogin {

    private final WebClient webClient;

    public NaverLogin(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://openapi.naver.com") // 기본 URL 설정
                .build();
    }

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public SocialProfile getUserInfo(String accessToken) {
        try {
            NaverPayload payload = webClient.get()
                    .uri("/v1/nid/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(NaverPayload.class)
                    .block();

            if (payload == null || payload.response() == null) {
                throw ErrorCode.INVALID_TOKEN.serviceException("네이버 사용자 정보 조회 실패");
            }

            return new SocialProfile(
                    payload.response().id(),
                    payload.response().email(),
                    payload.response().nickname(),
                    payload.response().name()
            );
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ErrorCode.INVALID_TOKEN.serviceException("네이버 인증 중 예외 발생: " + e.getMessage());
        }
    }
}
