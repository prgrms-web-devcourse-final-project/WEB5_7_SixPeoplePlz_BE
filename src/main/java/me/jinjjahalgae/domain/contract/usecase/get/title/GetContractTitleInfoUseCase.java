package me.jinjjahalgae.domain.contract.usecase.get.title;

import me.jinjjahalgae.domain.contract.usecase.get.title.dto.ContractTitleInfoResponse;

public interface GetContractTitleInfoUseCase {
    //유저가 감독자로 있는 계약의 정보 중 제목과 목표만
    ContractTitleInfoResponse execute(Long userId, Long contractId);
}