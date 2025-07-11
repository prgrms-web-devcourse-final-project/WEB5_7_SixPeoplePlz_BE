package me.jinjjahalgae.domain.contract.usecase.interfaces;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractRequest;
import me.jinjjahalgae.domain.contract.usecase.create.dto.CreateContractResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.create.CreateContractUseCaseImpl;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.mapper.ParticipationMapper;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // JUnit5에서 Mockito를 사용하겠다는 선언
class CreateContractUseCaseImplTest {

    @InjectMocks // 테스트 대상 클래스에 @Mock으로 선언된 가짜 객체들을 이 클래스에 주입
    private CreateContractUseCaseImpl contractCreateUseCase;

    @Mock // 가짜(Mock) 객체로 만들 의존성들
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractMapper contractMapper;

    @Mock
    private ParticipationMapper participationMapper;

    @Test
    @DisplayName("계약 생성 성공 테스트")
    void givenContractInfo_whenCreatingContract_thenReturnsCreatedContractInfo() {
        // Arrange (준비): 테스트 시나리오를 설정
        Long userId = 1L;
        CreateContractRequest request = new CreateContractRequest(
                "매일 운동하기", // title
                "매일 30분 이상 운동하기", // goal
                "치킨 못 먹기", // penalty
                "치킨 먹기", // reward
                3, // life
                3, // proofPerWeek
                false, // oneOff
                LocalDateTime.of(2025, 1, 1, 0, 0, 0), // startDate
                LocalDateTime.of(2025, 1, 31, 23, 59, 59), // endDate
                ContractType.BASIC, // type
                "signature.jpg" // signatureImageKey
        );

        // 가짜 객체들이 어떻게 행동할지 정의한다.
        User fakeUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스트유저")
                .build();

        Contract fakeContract = Contract.builder()
                .user(fakeUser)
                .startDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 31, 23, 59, 59))
                .title("매일 운동하기")
                .goal("매일 30분 이상 운동하기")
                .penalty("치킨 못 먹기")
                .reward("치킨 먹기")
                .life(3)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)
                .build();
        fakeContract.initialize();

        Participation fakeParticipation = Participation.builder()
                .contract(fakeContract)
                .user(fakeUser)
                .role(Role.CONTRACTOR)
                .valid(true)
                .imageKey("signature.jpg")
                .build();

        // 만약 userRepository.findById(userId)가 호출되면 fakeUser를 담은 Optional을 반환
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(fakeUser));

        // 만약 contractMapper.toEntity가 호출되면 fakeContract를 반환
        when(contractMapper.toEntity(any(User.class), any(CreateContractRequest.class))).thenReturn(fakeContract);

        // 만약 participationMapper.toEntity가 호출되면 fakeParticipation을 반환
        when(participationMapper.toEntity(any(Contract.class), any(User.class), any(String.class), any(Role.class), any(Boolean.class))).thenReturn(fakeParticipation);

        // 만약 contractRepository.save가 어떤 Contract 객체로든 호출되면 그 객체를 그대로 반환
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Act (실행): 테스트하려는 메서드를 호출
        CreateContractResponse response = contractCreateUseCase.execute(userId, request);

        // Assert (검증): 결과가 우리의 예상과 맞는지 확인
        assertThat(response).isNotNull();
        assertThat(response.contractUuid()).isEqualTo(fakeContract.getUuid());

        // 추가 검증: contractRepository의 save 메서드가 정확히 1번 호출되었는가?
        verify(contractRepository, times(1)).save(any(Contract.class));
        // 추가 검증: userRepository의 findById 메서드가 정확히 1번 호출되었는가?
        verify(userRepository, times(1)).findByIdAndDeletedAtIsNull(userId);

        System.out.println("테스트 성공!");
    }
}