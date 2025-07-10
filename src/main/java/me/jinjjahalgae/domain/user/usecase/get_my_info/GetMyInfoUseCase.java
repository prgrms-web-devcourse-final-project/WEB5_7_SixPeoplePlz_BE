package me.jinjjahalgae.domain.user.usecase.get_my_info;

import me.jinjjahalgae.domain.user.usecase.common.dto.MyInfoResponse;

public interface GetMyInfoUseCase {
    MyInfoResponse execute(Long userId);
} 