package me.jinjjahalgae.domain.invite.usecase;

import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class VerifyInviteLinkUseCaseImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private VerifyInviteLinkUseCaseImpl verifyInviteLinkUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 초대코드 검증에 성공한다")
    void verifyValidInviteLink_Success() {
        // given
        String inviteCode = "valid123";
        when(redisTemplate.hasKey(inviteCode)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> verifyInviteLinkUseCase.execute(inviteCode));
    }

    @Test
    @DisplayName("만료되었거나 없는 초대코드는 예외를 발생시킨다")
    void verifyInvalidInviteLink_ThrowsException() {
        // given
        String inviteCode = "invalid123";
        when(redisTemplate.hasKey(inviteCode)).thenReturn(false);

        // when & then
        assertThrows(AppException.class, () -> verifyInviteLinkUseCase.execute(inviteCode));
    }
}