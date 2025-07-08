package me.jinjjahalgae.global.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        title = "진짜할게 Jwt 페어",
        description = "진짜할게 Jwt (AccessToken, RefreshToken) <br> accessToken - expiration (30m) <br> refreshToken - expiration (30m)",
        example = """
        {
          "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF12345zUxODMyNjMxLCJleHAiOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0",
          "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNzUxODMyNjMx123456iOjE3NTE4MzQ0MzF9.hY7PLaNrQifTgHUjg8Jb2899FQfCSoiGGJv6-yl6jS0"
        }"""
)
public record Token(
        String accessToken,
        String refreshToken
) {
}
