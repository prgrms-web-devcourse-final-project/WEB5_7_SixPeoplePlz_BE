package me.jinjjahalgae.global.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class LogTraceAspect {
    private final LogTrace logTrace;
    private final ObjectMapper objectMapper;
    private final LogVersionInfo logVersionInfo;

    @Around("execution(* me.jinjjahalgae.domain..*(..)) || execution(* me.jinjjahalgae.presentation.api..*(..)) || execution(* me.jinjjahalgae.global.scheduler..*(..))" )
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("[LogTraceAspect] AOP 진입, 메서드: {}", joinPoint.getSignature().toShortString());
        }
        TraceStatus status = null;
        try {
            // 메서드의 전체 패키지/클래스/메서드 경로
            String methodPath = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            status = logTrace.begin(methodPath);

            // 메서드 파라미터 정보 : 개발/로컬에서만 남김 (불필요한 로그 최소화)
            if (log.isDebugEnabled()) {
                String methodInfo = getMethodInfo(joinPoint);
                log.debug("[{}] Parameters: {}", status.getTraceId().getId(), methodInfo);
            }

            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    private String getMethodInfo(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        String params = getParams(signature, joinPoint.getArgs());
        return methodName + " " + params;
    }

    private String getParams(MethodSignature signature, Object[] args) {
        String[] paramNames = signature.getParameterNames();
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
            String value = convertToString(args[i]);
            paramList.add(name + "=" + value);
        }
        return "[" + String.join(", ", paramList) + "]";
    }

    private String convertToString(Object arg) {
        try {
            if (arg instanceof CustomJwtPrincipal oauthUser) return maskSensitiveInfo(oauthUser);
            if (arg instanceof User user) return maskUserInfo(user);
            return objectMapper.writeValueAsString(arg);
        } catch (JsonProcessingException e) {
            return arg != null ? arg.toString() : "null";
        }
    }

    private String maskSensitiveInfo(CustomJwtPrincipal oauthUser) {
        return String.format(
                "CustomOAuth2User{id=%s, name=%s}",
                oauthUser.getUser().getId(),
                oauthUser.getUser().getName()
        );
    }

    private String maskUserInfo(User user) {
        return String.format(
                "User{id=%s, name=%s}",
                user.getId(),
                user.getName()
        );
    }
}
