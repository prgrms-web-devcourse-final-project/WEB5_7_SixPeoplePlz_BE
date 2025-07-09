package me.jinjjahalgae.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.global.exception.ErrorCode;
import me.jinjjahalgae.global.exception.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        // statusCode 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 응답 컨텐츠 타입 및 인코딩 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ApiResponse 타입 json 응답 생성
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

        ErrorResponse errorResponseDto = ErrorResponse.from(errorCode);


        // json으로 response
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDto));
    }
}