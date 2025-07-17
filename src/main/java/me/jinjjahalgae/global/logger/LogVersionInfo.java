package me.jinjjahalgae.global.logger;




public record LogVersionInfo(
        String serviceName,
        String version,
        String commitId,
        String buildTime
) { }
