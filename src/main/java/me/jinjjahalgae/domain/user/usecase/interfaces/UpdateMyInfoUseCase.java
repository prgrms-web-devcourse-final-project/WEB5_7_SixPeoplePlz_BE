package me.jinjjahalgae.domain.user.usecase.interfaces;

import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.domain.user.dto.UpdateMyInfoRequest;

public interface UpdateMyInfoUseCase {
    MyInfoResponse execute(Long userId, UpdateMyInfoRequest request);
} 