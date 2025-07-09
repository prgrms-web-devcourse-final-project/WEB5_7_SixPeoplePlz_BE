package me.jinjjahalgae.domain.invite.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jinjjahalgae.domain.invite.dto.request.InviteLinkVerifyRequest;
import me.jinjjahalgae.domain.invite.dto.response.ContractUuidResponse;
import me.jinjjahalgae.domain.invite.model.InviteInfo;
import me.jinjjahalgae.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class VerifyInvitePasswordUseCaseImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @InjectMocks
    private VerifyInvitePasswordUseCaseImpl verifyInvitePasswordUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(verifyInvitePasswordUseCase, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("올바른 비밀번호로 검증에 성공하고 UUID를 반환한다")
    void verifyCorrectPassword_Success() {
        // given
        String inviteCode = "valid123";
        String password = "password123";
        String contractUuid = "contract-uuid-123";
        InviteLinkVerifyRequest request = new InviteLinkVerifyRequest(password);
        InviteInfo inviteInfo = new InviteInfo(contractUuid, password);

        when(valueOperations.get(inviteCode)).thenReturn(objectMapper.convertValue(inviteInfo, java.util.Map.class));

        // when
        ContractUuidResponse result = verifyInvitePasswordUseCase.execute(inviteCode, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.contractUuid()).isEqualTo(contractUuid);
    }

    @Test
    @DisplayName("틀린 비밀번호로 검증 시 예외를 발생시킨다")
    void verifyWrongPassword_ThrowsException() {
        // given
        String inviteCode = "valid123";
        String correctPassword = "password123";
        String wrongPassword = "wrongPassword";
        String contractUuid = "contract-uuid-123";
        InviteLinkVerifyRequest request = new InviteLinkVerifyRequest(wrongPassword);
        InviteInfo inviteInfo = new InviteInfo(contractUuid, correctPassword);

        when(valueOperations.get(inviteCode)).thenReturn(objectMapper.convertValue(inviteInfo, java.util.Map.class));

        // when & then
        assertThrows(AppException.class, () -> verifyInvitePasswordUseCase.execute(inviteCode, request));
    }
}