package me.jinjjahalgae.domain.invite.dto.response;

/**
 * 초대링크 반환을 위한 response
 *
 * @param inviteUrl 초대링크
 * @param password 비밀번호
 */
public record InviteLinkResponse(
        String inviteUrl,
        String password
) {
}
