package me.jinjjahalgae.domain.contract.usecase.get.preview;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.contract.usecase.get.common.ContractBasicResponse;
import me.jinjjahalgae.domain.participation.usecase.common.ParticipantFullResponse;

import java.util.List;

public record ContractPreviewResponse(

        @Schema(description = "계약 조회 공통 데이터")
        ContractBasicResponse contractBasicResponse,

        @Schema(description = "계약서 디자인 타입", example = "BASIC")
        ContractType type, //계약서 디자인

        @Schema(description = "참여자 정보", example = "참여자 정보")
        List<ParticipantFullResponse> participants //참여한 유저 정보
){}