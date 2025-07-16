package me.jinjjahalgae.domain.user.mapper;

import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.usecase.common.dto.MyInfoResponse;
import org.springframework.stereotype.Component;

/**
 * User 도메인 매퍼
 */
@Component
public class UserMapper {

    /**
     * User 엔티티를 MyInfoResponse로 매핑
     */
    public MyInfoResponse toMyInfoResponse(User user) {
        return new MyInfoResponse(
            user.getId(),
            user.getName(),
            user.getNickname(),
            user.getEmail()
        );
    }
} 