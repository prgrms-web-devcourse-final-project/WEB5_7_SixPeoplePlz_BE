package me.jinjjahalgae.domain.auth.usecase.testlogin;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.auth.repository.AuthRepository;
import me.jinjjahalgae.domain.auth.entity.Auth;
import me.jinjjahalgae.domain.auth.enums.Provider;
import me.jinjjahalgae.global.security.jwt.JwtTokenProvider;
import me.jinjjahalgae.domain.auth.usecase.login.social.dto.SocialLoginResponse;
import me.jinjjahalgae.global.security.jwt.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestLoginUseCaseImpl implements TestLoginUseCase {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String TEST_USER_EMAIL = "test@test.com";
    private static final String TEST_USER_NICKNAME = "테스트유저";

    @Override
    @Transactional
    public SocialLoginResponse execute() {
        User user = userRepository.findByEmail(TEST_USER_EMAIL)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(TEST_USER_NICKNAME)
                            .nickname(TEST_USER_NICKNAME)
                            .email(TEST_USER_EMAIL)
                            .build();
                    return userRepository.save(newUser);
                });

        Auth auth = authRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Auth newAuth = Auth.builder()
                            .provider(Provider.KAKAO)
                            .oauthId("test_" + user.getId())
                            .userId(user.getId())
                            .build();
                    return authRepository.save(newAuth);
                });

        Token token = jwtTokenProvider.generateToken(user.getId());

        auth.updateRefreshToken(token.refreshToken());

        return new SocialLoginResponse(token.accessToken(), token.refreshToken());
    }
} 