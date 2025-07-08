package me.jinjjahalgae.domain.user.usecase.interfaces;

import me.jinjjahalgae.domain.user.dto.MyInfoResponse;

public interface GetMyInfoUseCase {
    MyInfoResponse execute(Long userId);
} 