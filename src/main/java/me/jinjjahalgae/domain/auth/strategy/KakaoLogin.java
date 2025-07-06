package me.jinjjahalgae.domain.auth.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.dto.social.KakaoPayload;
import me.jinjjahalgae.domain.auth.dto.social.SocialProfile;
import me.jinjjahalgae.domain.auth.enums.Provider;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
        // 카카오 API 호출해서 사용자 정보 받아오기
        KakaoPayload payload = webClient.get()
                .uri("/v2/user/me") // 기본 URL 뒤에 붙는 상세 경로
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoPayload.class)
                .block();

        // 예외 처리
        if (payload == null) {
            throw ErrorCode.INVALID_TOKEN.serviceException("카카오 사용자 정보 조회 실패");
        }

        // SocialProfile로 반환
        return new SocialProfile(
                String.valueOf(payload.id()),
                payload.kakaoAccount().email(),
                payload.kakaoAccount().profile().nickname(),
                payload.kakaoAccount().profile().nickname()
        );
    }
}
