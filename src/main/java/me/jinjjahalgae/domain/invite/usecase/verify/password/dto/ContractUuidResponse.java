package me.jinjjahalgae.domain.invite.usecase.verify.password.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 초대링크의 비밀번호가 맞는 경우 response
 * @param contractUuid 계약 uuid
 */
@Schema(
        title = "계약서 Uuid 응답",
        description = "비밀번호가 맞는 경우 계약서의 Uuid 응답 DTO",
        example = """
        {
          "contractUuid": "36865103-5d08-4139-ba4a-b32da2316d7f"
        }
        """
)
public record ContractUuidResponse(
        @Schema(description = "계약의 Uuid")
        String contractUuid
) {
}
