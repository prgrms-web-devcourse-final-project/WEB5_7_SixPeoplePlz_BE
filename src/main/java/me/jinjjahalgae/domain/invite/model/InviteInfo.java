package me.jinjjahalgae.domain.invite.model;

/**
 * redis에 저장되는 초대 정보 record
 * @param contractUuid 계약의 uuid
 * @param password 비밀번호
 */
public record InviteInfo(
        String contractUuid,
        String password
) {
}
