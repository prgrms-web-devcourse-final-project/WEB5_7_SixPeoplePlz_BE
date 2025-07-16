package me.jinjjahalgae.global.security.jwt;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomJwtPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomJwtPrincipal loadUserByUsername(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.serviceException("토큰 sub로 유저를 찾을 수 없음 ID: " + userId));

        return new CustomJwtPrincipal(user);
    }
}
