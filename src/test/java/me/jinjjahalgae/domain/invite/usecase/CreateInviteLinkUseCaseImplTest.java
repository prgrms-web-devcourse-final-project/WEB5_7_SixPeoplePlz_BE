//package me.jinjjahalgae.domain.invite.usecase;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import me.jinjjahalgae.domain.contract.entity.Contract;
//import me.jinjjahalgae.domain.contract.repository.ContractRepository;
//import me.jinjjahalgae.domain.invite.model.InviteInfo;
//import me.jinjjahalgae.domain.invite.usecase.create.invite.dto.InviteLinkResponse;
//import me.jinjjahalgae.domain.invite.usecase.create.invite.CreateInviteLinkUseCaseImpl;
//import me.jinjjahalgae.domain.invite.util.InviteTestUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class CreateInviteLinkUseCaseImplTest {
//
//    @Mock
//    private ContractRepository contractRepository;
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//    @Mock
//    private ValueOperations<String, Object> valueOperations;
//    @InjectMocks
//    private CreateInviteLinkUseCaseImpl createInviteLinkUseCase;
//
//    // ObjectMapper는 실제 인스턴스를 주입하여 Map <-> Object 변환을 테스트
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        // 실제 ObjectMapper 인스턴스를 useCase에 주입
//        ReflectionTestUtils.setField(createInviteLinkUseCase, "objectMapper", objectMapper);
//
//        // @Value 필드 값 설정
//        ReflectionTestUtils.setField(createInviteLinkUseCase, "INVITE_URL_PREFIX", "http://test.com/");
//        ReflectionTestUtils.setField(createInviteLinkUseCase, "CONTRACT_TO_INVITE_PREFIX", "contract:invite:");
//        ReflectionTestUtils.setField(createInviteLinkUseCase, "SUPERVISOR_COUNT_PREFIX", "contract:supervisors:");
//        ReflectionTestUtils.setField(createInviteLinkUseCase, "MAX_SUPERVISORS", 5);
//        ReflectionTestUtils.setField(createInviteLinkUseCase, "INVITE_EXPIRATION_HOURS", 24L);
//    }
//
//    @Test
//    @DisplayName("신규 초대링크를 정상적으로 생성한다")
//    void createNewInviteLink_Success() {
//        // given
//        Long contractId = 1L;
//        Contract contract = InviteTestUtil.createContract(contractId, InviteTestUtil.createUser(1L, "userA", "userA_nick"));
//        String contractKey = "contract:invite:" + contractId;
//
//        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
//        when(valueOperations.get(contractKey)).thenReturn(null); // 기존 링크 없음
//
//        // when
//        InviteLinkResponse result = createInviteLinkUseCase.execute(contractId);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.inviteUrl()).startsWith("http://test.com/");
//        assertThat(result.password()).isNotBlank().hasSize(8);
//
//        verify(valueOperations, times(2)).set(anyString(), any(), anyLong(), any());
//        verify(valueOperations, times(1)).setIfAbsent("contract:supervisors:" + contractId, 5);
//    }
//
//    @Test
//    @DisplayName("기존에 유효한 초대링크가 있으면 재사용한다")
//    void reuseExistingInviteLink_Success() {
//        // given
//        Long contractId = 1L;
//        String existingInviteCode = "existing123";
//        Contract contract = InviteTestUtil.createContract(contractId, InviteTestUtil.createUser(1L, "userA", "userA_nick"));
//        InviteInfo existingInfo = new InviteInfo(contract.getUuid(), "password123");
//        String contractKey = "contract:invite:" + contractId;
//
//        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
//        when(valueOperations.get(contractKey)).thenReturn(existingInviteCode);
//        // ObjectMapper가 Map으로 변환할 것을 가정하여 Map 객체 반환
//        when(valueOperations.get(existingInviteCode)).thenReturn(objectMapper.convertValue(existingInfo, java.util.Map.class));
//
//        // when
//        InviteLinkResponse result = createInviteLinkUseCase.execute(contractId);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.inviteUrl()).isEqualTo("http://test.com/" + existingInviteCode);
//        assertThat(result.password()).isEqualTo("password123");
//
//        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
//        verify(valueOperations, never()).setIfAbsent(anyString(), anyInt());
//    }
//}