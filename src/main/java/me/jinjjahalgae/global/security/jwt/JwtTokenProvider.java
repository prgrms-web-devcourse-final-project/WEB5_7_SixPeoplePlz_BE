package me.jinjjahalgae.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final CustomJwtPrincipalService customJwtPrincipalService;
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenProvider(JwtProperties jwtProperties, CustomJwtPrincipalService customJwtPrincipalService) {
        this.jwtProperties = jwtProperties;
        this.customJwtPrincipalService = customJwtPrincipalService;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public Token generateToken(Long userId) {
        String accessToken = issueAccessToken(userId);
        String refreshToken = issueRefreshToken(userId);

        return new Token(accessToken, refreshToken);
    }

    public String issueAccessToken(Long id) {
        return issue(id, jwtProperties.getAccessTokenExpiration());
    }

    public String issueRefreshToken(Long id) {
        return issue(id, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * JWT 토큰 발급
     */
    private String issue(Long id, Duration exp) {
        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + exp.toMillis());

        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 검증 + Claims 반환
     */
    public Claims validateToken(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw ErrorCode.EXPIRED_TOKEN.serviceException("만료된 토큰입니다.");
        } catch (JwtException e) {
            throw ErrorCode.INVALID_TOKEN.serviceException("유효하지 않은 JWT: " + e.getMessage());
        }
    }

    /**
     * token 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * jwt에서 userId 추출
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);

        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰으로부터 Authentication 객체를 생성
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 userId 추출
        Long userId = getUserIdFromToken(token);

        // customJwtPrincipalService를 통해 CustomJwtPrincipal 생성
        CustomJwtPrincipal userPrincipal = customJwtPrincipalService.loadUserByUsername(userId.toString());

        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }
}
