package me.jinjjahalgae.domain.user.usecase.update_my_info;

import me.jinjjahalgae.domain.user.usecase.common.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.usecase.update_my_info.dto.UpdateMyInfoRequest;

public interface UpdateMyInfoUseCase {
    MyInfoResponse execute(Long userId, UpdateMyInfoRequest request);
} 