package me.jinjjahalgae.domain.invite.dto;

/**
 *
 * @param contractUuid
 * @param password
 */
public record InviteInfo(
        String contractUuid,
        String password
) {
}
