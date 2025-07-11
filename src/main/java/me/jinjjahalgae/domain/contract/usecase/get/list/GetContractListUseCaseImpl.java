package me.jinjjahalgae.domain.contract.usecase.get.list;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.mapper.ContractMapper;
import me.jinjjahalgae.domain.contract.repository.ContractRepository;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContractListUseCaseImpl implements GetContractListUseCase {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    public Page<ContractListResponse> execute(Long userId, List<ContractStatus> statuses, Pageable pageable) {
        //유저 id와 계약의 상태를 받아서 조회 -> 레포지토리에 필요, 페이징으로 구현
        Page<Contract> contractPage = contractRepository.findByUserIdAndStatusInOrderByIdDesc(userId, statuses, pageable);

        return contractPage
                .map(contractMapper::toListResponse);
    }
}
