package me.jinjjahalgae.domain.invite.dto.response;

/**
 *
 * @param inviteUrl
 * @param password
 */
public record CreateInviteLinkResponse(
        String inviteUrl,
        String password
) {
}
