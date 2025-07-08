package me.jinjjahalgae.domain.auth.usecase.interfaces;

import me.jinjjahalgae.domain.auth.dto.refresh.RefreshRequest;
import me.jinjjahalgae.domain.auth.dto.refresh.RefreshResponse;

public interface RefreshTokenUseCase {
    RefreshResponse execute(RefreshRequest request);
} 