package me.jinjjahalgae.domain.user.usecase.update.myinfo;

import me.jinjjahalgae.domain.user.usecase.common.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.usecase.update.myinfo.dto.UpdateMyInfoRequest;

public interface UpdateMyInfoUseCase {
    MyInfoResponse execute(Long userId, UpdateMyInfoRequest request);
} 