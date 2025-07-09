package me.jinjjahalgae.domain.invite.dto.request;

/**
 * 초대링크 비밀번호 확인 request
 * @param password 비밀번호
 */
public record InviteLinkVerifyRequest(
        String password
) {
}
