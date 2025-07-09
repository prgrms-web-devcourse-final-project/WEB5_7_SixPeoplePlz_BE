package me.jinjjahalgae.domain.contract.usecase.interfaces;

import jakarta.validation.constraints.NotBlank;
import me.jinjjahalgae.domain.contract.dto.request.ContractCreateRequest;
import me.jinjjahalgae.domain.contract.dto.response.ContractCreateResponse;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.ContractCreateUseCase;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.participation.mapper.ParticipationMapper;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractCreateUseCaseImpl implements ContractCreateUseCase {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    @Override
    public ContractCreateResponse execute(Long userId, ContractCreateRequest request) {
        //유저 검증하고
        User user = findUserById(userId);
        //계약 생성
        Contract contract = ContractMapper.toEntity(user, request);
        //계약자가 계약에 서명하고 저장
        Participation contractorSignature = ParticipationMapper.toEntity(
                contract, user, request.signatureImageKey(), Role.CONTRACTOR, true
        );
        //저장
        Contract saveContract = contractRepository.save(contract);
        //반환
        return new ContractCreateResponse(saveContract.getId(), saveContract.getUuid());
    }

    private User findUserById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> ErrorCode.USER_NOT_FOUND.domainException("유저를 찾을 수 없음"));
    }
}