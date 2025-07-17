package me.jinjjahalgae.global.logger;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("log.version-info")
public record LogVersionInfoProperties(
        String serviceName,
        String version,
        String commitId,
        String buildTime
) {
}
