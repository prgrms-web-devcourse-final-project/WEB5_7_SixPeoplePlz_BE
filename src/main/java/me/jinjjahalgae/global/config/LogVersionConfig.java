package me.jinjjahalgae.global.config;

import me.jinjjahalgae.global.logger.LogVersionInfo;
import me.jinjjahalgae.global.logger.LogVersionInfoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogVersionConfig {
    @Bean
    public LogVersionInfo logVersionInfo(LogVersionInfoProperties props) {
        return new LogVersionInfo(
                props.serviceName(),
                props.version(),
                props.commitId(),
                props.buildTime()
        );
    }
}
