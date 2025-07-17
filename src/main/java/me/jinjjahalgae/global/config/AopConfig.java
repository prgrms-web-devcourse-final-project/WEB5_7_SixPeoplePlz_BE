package me.jinjjahalgae.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jinjjahalgae.global.logger.LogTrace;
import me.jinjjahalgae.global.logger.LogTraceAspect;
import me.jinjjahalgae.global.logger.LogVersionInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {
    @Bean
    public LogTraceAspect logTraceAspect(
            LogTrace logTrace,
            ObjectMapper objectMapper,
            LogVersionInfo logVersionInfo
    ) {
        return new LogTraceAspect(logTrace, objectMapper, logVersionInfo);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
