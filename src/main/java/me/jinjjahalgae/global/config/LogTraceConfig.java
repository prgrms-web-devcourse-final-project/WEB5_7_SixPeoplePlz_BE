package me.jinjjahalgae.global.config;

import jakarta.servlet.http.HttpServletRequest;
import me.jinjjahalgae.global.logger.LogTrace;
import me.jinjjahalgae.global.logger.LogVersionInfo;
import me.jinjjahalgae.global.logger.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {

    @Bean
    public LogTrace logTrace(LogVersionInfo logVersionInfo, HttpServletRequest request) {
        return new ThreadLocalLogTrace(logVersionInfo, request);
    }

}
