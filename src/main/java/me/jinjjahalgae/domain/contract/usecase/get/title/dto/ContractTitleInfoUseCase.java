package me.jinjjahalgae.domain.contract.usecase.get.title.dto;

public interface ContractTitleInfoUseCase {
    //유저가 감독자로 있는 계약의 정보 중 제목과 목표만
    ContractTitleInfoResponse execute(Long userId, Long contractId);
}