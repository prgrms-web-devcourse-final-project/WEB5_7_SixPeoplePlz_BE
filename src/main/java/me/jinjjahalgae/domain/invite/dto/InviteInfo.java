package me.jinjjahalgae.domain.invite.dto;

/**
 * redis에 저장되는 초대 정보
 * @param contractUuid 계약의 uuid
 * @param password 비밀번호
 */
public record InviteInfo(
        String contractUuid,
        String password
) {
}
