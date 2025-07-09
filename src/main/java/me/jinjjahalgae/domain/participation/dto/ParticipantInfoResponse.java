package me.jinjjahalgae.domain.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;

@Schema(
    title = "참여자 정보 응답",
    description = "계약에 참여하는 사용자의 정보를 담은 DTO"
)
public record ParticipantInfoResponse(
    @Schema(description = "참여자 이름",
        example = "홍길동",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String userName,
    
    @Schema(description = "참여자 ID",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId,
    
    @Schema(description = "참여자 역할",
        example = "CONTRACTOR",
        requiredMode = Schema.RequiredMode.REQUIRED)
    Role role,

    @Schema(description = "역할 유효 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    Boolean valid
) {

    /// Participation -> ParticipantInfoResponse로 변환
    public static ParticipantInfoResponse from (Participation participation) {
        return new ParticipantInfoResponse(
                participation.getUser().getName(),
                participation.getUser().getId(),
                participation.getRole(),
                participation.getValid()
        );
    }
}
