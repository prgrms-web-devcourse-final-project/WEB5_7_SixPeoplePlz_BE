package me.jinjjahalgae.domain.contract.usecase.get.title;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractTitleInfoUseCaseImpl implements ContractTitleInfoUseCase {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(readOnly = true)
    public ContractTitleInfoResponse execute(Long userId, Long contractId) {

        //유효한 참가자이면 감독자 상세 페이지 조회 (검증 : 참가자인가??)
        //참가자는 계약 테이블이 아닌 참가 테이블과 조인 계야 아이디로 참가 테이블에서 계약 아이디가 같은 것들을 찾고 거기서 유저 id 검사
        Contract contract = contractRepository.findValidParticipantByIdAndUserId(contractId, userId)
                .orElseThrow(() -> ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다."));

        return contractMapper.toTitleInfoResponse(contract);
    }
}
