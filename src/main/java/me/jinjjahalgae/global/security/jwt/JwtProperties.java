package me.jinjjahalgae.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String secret;
    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;
}
