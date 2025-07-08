package me.jinjjahalgae.domain.contract.usecase.interfaces;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.ContractCreateUseCase;
import me.jinjjahalgae.domain.user.User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractCreateUseCaseImpl implements ContractCreateUseCase {

    private final ContractRepository contractRepository;
    //머지 이후 추가 구현
    @Override
    public ContractCreateResponse execute(Long userId, ContractCreateRequest request) {
        //유저 검증하고
        User user = findUserById(userId);
        //계약 생성하고 저장
        Contract contract = createContractAndSave(user, request);
        //계약자 서명하고 진짜 저장
        createContractWithSignature(contract, user, request.signatureImageKey());
        //반환
        return new ContractCreateResponse(contract.getId(), contract.getUuid());
    }

    private User findUserById(Long userId) {
        return null;
    }
}