package me.jinjjahalgae.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 내 정보 조회 응답 dto
 */
@Schema(
        title = "내 유저정보 응답",
        description = "내 유저정보 응답 DTO"
)
public record MyInfoResponse(
    @Schema(description = "유저 ID", example = "1")
    Long id,
            
    @Schema(description = "이름", example = "홍길동")
    String name,
            
    @Schema(description = "닉네임", example = "홍길동")
    String nickname,
            
    @Schema(description = "이메일", example = "user@example.com")
    String email
) {} 