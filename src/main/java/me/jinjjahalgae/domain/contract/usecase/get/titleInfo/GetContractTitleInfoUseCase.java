package me.jinjjahalgae.domain.contract.usecase.get.titleInfo;

import me.jinjjahalgae.domain.contract.usecase.get.titleInfo.dto.TitleInfoResponse;

public interface GetContractTitleInfoUseCase {

    TitleInfoResponse execute(Long userId, Long contractId);
}
