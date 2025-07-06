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

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return "";
    }

    public Long getUserId(){
        return user.getId();
    }
}
