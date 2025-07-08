package me.jinjjahalgae.domain.invite.dto.response;

/**
 * 초대링크의 비밀번호가 맞는 경우 response
 * @param contractUuid 계약 uuid
 */
public record ContractUuidResponse(
        String contractUuid
) {
}
