package me.jinjjahalgae.global.config;

import jakarta.servlet.http.HttpServletRequest;
import me.jinjjahalgae.global.logger.LogTrace;
import me.jinjjahalgae.global.logger.LogVersionInfo;
import me.jinjjahalgae.global.logger.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class LogTraceConfig {
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HttpServletRequest httpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @Bean
    public LogTrace logTrace(LogVersionInfo logVersionInfo, HttpServletRequest request) {
        return new ThreadLocalLogTrace(logVersionInfo, request);
    }

}
