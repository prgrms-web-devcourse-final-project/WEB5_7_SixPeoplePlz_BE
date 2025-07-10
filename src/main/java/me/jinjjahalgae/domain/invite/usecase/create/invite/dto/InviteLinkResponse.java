package me.jinjjahalgae.domain.invite.usecase.create.invite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 초대링크 반환을 위한 response
 * @param inviteUrl 초대링크
 * @param password 비밀번호
 */
@Schema(
        title = "초대 링크 생성 응답",
        description = "계약의 초대 링크와 비밀번호 응답 DTO",
        example = """
        {
          "inviteUrl": "https://jinjjahalgae.xyz/api/invite/da2316d7",
          "password": "ac08ee16"
        }
        """
)
public record InviteLinkResponse(
        @Schema(description = "초대 링크")
        String inviteUrl,
        @Schema(description = "초대 링크 접속 비밀번호")
        String password
) {
}
