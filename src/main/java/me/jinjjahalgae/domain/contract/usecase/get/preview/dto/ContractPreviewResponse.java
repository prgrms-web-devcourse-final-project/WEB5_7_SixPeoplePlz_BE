package me.jinjjahalgae.domain.contract.usecase.get.preview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.usecase.get.detail.dto.ContractDetailResponse;

import java.util.List;

public record ContractPreviewResponse(

        @Schema(description = "계약 제목, 기간 등 계약서의 세부 정보")
        ContractDetailResponse contractDetail,

        @Schema(description = "서명 정보 리스트 (유저이름, 역할, 이미지 키")
        List<SignatureInfoResponse> signatureInfoList
) {

}
