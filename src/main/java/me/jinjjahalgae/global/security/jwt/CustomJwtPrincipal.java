package me.jinjjahalgae.global.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomJwtPrincipal implements UserDetails {
    // 진짜할게 User
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // JWT 방식 사용
    @Override
    public String getPassword() {
        return null;
    }

    // JWT 방식 사용
    @Override
    public String getUsername() {
        return "";
    }

    // getUsername을 사용하지 않고 명시적으로 userId 가져오기 위함
    public Long getUserId(){
        return user.getId();
    }
}
