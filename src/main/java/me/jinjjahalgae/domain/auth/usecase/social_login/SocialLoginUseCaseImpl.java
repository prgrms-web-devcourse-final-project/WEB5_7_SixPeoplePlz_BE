package me.jinjjahalgae.domain.auth.usecase.social_login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.auth.Auth;
import me.jinjjahalgae.domain.auth.AuthRepository;
import me.jinjjahalgae.domain.auth.mapper.AuthMapper;
import me.jinjjahalgae.domain.auth.usecase.social_login.dto.SocialLoginRequest;
import me.jinjjahalgae.domain.auth.usecase.social_login.dto.SocialLoginResponse;
import me.jinjjahalgae.domain.auth.model.SocialProfile;
import me.jinjjahalgae.domain.auth.strategy.SocialLogin;
import me.jinjjahalgae.domain.auth.strategy.SocialLoginFactory;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.security.jwt.JwtTokenProvider;
import me.jinjjahalgae.global.security.jwt.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginUseCaseImpl implements SocialLoginUseCase {
    private final SocialLoginFactory socialLoginFactory;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Override
    @Transactional
    public SocialLoginResponse execute(SocialLoginRequest req) {
        String provider = req.provider();
        String thirdPartyAccessToken = req.accessToken();

        // 소셜 로그인 전략 추출
        SocialLogin socialLogin = socialLoginFactory.getStrategy(provider);

        log.info("socialLogin: {}", socialLogin);

        // 써드파티 토큰으로 공통 페이로드 추출 (sub, email, nickname)
        SocialProfile socialProfile = socialLogin.getUserInfo(thirdPartyAccessToken);

        log.info("socialProfile: {}", socialProfile);

        // 추출한 써드파티 id로 auth 테이블 조회 (존재하는 유저인지 확인)
        Optional<Auth> optionalAuth = authRepository.findByOauthId(socialProfile.socialId());

        Auth auth;

        if (optionalAuth.isPresent()) {
            // 이미 가입된 유저인 경우 로그인
            auth = optionalAuth.get();
        } else {
            // 가입되지 않은 유저인 경우 신규 회원가입
            User newUser = User.builder()
                    .name(socialProfile.name())
                    .nickname(socialProfile.nickname())
                    .email(socialProfile.email())
                    .build();

            userRepository.save(newUser);

            auth = Auth.builder()
                    .provider(socialLogin.getProvider())
                    .oauthId(socialProfile.socialId())
                    .userId(newUser.getId())
                    .build();

            authRepository.save(auth);
        }

        // 토큰 생성
        Token token = jwtTokenProvider.generateToken(auth.getUserId());

        // 리프레시 토큰 갱신
        auth.updateRefreshToken(token.refreshToken());

        return authMapper.toSocialLoginResponse(token);
    }
}
