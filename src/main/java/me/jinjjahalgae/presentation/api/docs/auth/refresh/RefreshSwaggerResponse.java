package me.jinjjahalgae.presentation.api.docs.auth.refresh;

import me.jinjjahalgae.domain.auth.dto.refresh.RefreshResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class RefreshSwaggerResponse extends CommonResponse<RefreshResponse> {
    public RefreshSwaggerResponse(RefreshResponse result) {
        super(result);
    }
} 