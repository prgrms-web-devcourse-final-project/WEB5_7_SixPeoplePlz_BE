package me.jinjjahalgae.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.exception.AppException;
import me.jinjjahalgae.global.exception.ErrorCode;
import me.jinjjahalgae.global.exception.ErrorResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (isExcludedUrl(req)) {
            filterChain.doFilter(req, res);

            return;
        }

        //Authorization 헤더에서 Bearer 토큰 추출
        String accessToken = jwtTokenProvider.resolveToken(req);

        if (accessToken == null) {
            filterChain.doFilter(req, res);

            return;
        }

        try{
            // 토큰에서 유저 Principal 추출 (내부적으로 토큰 검증 진행)
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

            // SecurityContextHolder에 Principal 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //다음 필터로 요청 전달
            filterChain.doFilter(req, res);
        } catch (AppException e) {
            // JwtTokenProvider에서 던진 커스텀 예외를 잡아서 처리
            log.warn("JWT 인증 예외: {}", e.getErrorCode().getMessage());

            setErrorResponse(res, e.getErrorCode());
        } catch (Exception e) {
            // JwtTokenProvider에서 던진 커스텀 예외를 잡아서 처리
            log.error("예상하지 못한 예외: {}", e.getMessage());

            setErrorResponse(res, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isExcludedUrl(HttpServletRequest req) {
        String uri = req.getRequestURI();

        return uri.startsWith("/api/auth/");
    }

    // 에러 응답을 생성
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponseDto = ErrorResponse.from(errorCode);
        CommonResponse<Object> commonResponse = CommonResponse.error(errorResponseDto);

        response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
    }
}
