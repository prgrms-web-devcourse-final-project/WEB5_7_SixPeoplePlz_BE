package me.jinjjahalgae.global.security.jwt;

public record Token(
        String accessToken,
        String refreshToken
) {
}
