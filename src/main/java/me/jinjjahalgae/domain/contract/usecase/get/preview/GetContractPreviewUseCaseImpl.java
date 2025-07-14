package me.jinjjahalgae.domain.contract.usecase.get.preview;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.preview.dto.ContractPreviewResponse;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.repository.ParticipationRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetContractPreviewUseCaseImpl implements GetContractPreviewUseCase {

    private final ParticipationRepository participationRepository;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    /**
     * 계약서 보기를 눌렀을 때 계약서 화면을 위한 usecase (계약자 감독자 케이스 둘다 포함)
     * 컨트롤러로부터 계약id와 principal로 userid를 넘겨받고 계약서 이미지 미리보기를 위한 정보를 반환
     * @param userId 현재 로그인 한 사용자의 id
     * @param contractId 미리보기를 열람하려는 계약서의 id
     * @return ContractPreviewResponse (계약서 디테일 정보, [참여자 이름, role, 서명]의 리스트)
     */
    @Override
    @Transactional(readOnly = true)
    public ContractPreviewResponse execute(Long userId, Long contractId) {

        // contractId로 계약 가져옴
        Contract contract = contractRepository.findContractById(contractId)
                .orElseThrow(() -> ErrorCode.CONTRACT_NOT_FOUND.serviceException("존재하지 않는 계약id: " + contractId));

        // 해당 userid가 계약에 참여자로 존재하는지 검사
        boolean isParticipated = participationRepository.existsByContractIdAndUserIdAndValidIsTrue(contractId, userId);
        if ( !isParticipated ) {
            throw ErrorCode.ACCESS_DENIED.serviceException("참여자로 등록되어있지 않은 계약서를 열람 시도함");
        }

        // 모든 참여자들 리스트를 계약id로 조회 (감독포기한 감독자들 포함)
        List<Participation> participationList = participationRepository.findByContractId(contractId);

        // previewResponse로 매핑하여 반환
        return contractMapper.mapToContractPreviewResponse(contract, participationList);
    }

}
