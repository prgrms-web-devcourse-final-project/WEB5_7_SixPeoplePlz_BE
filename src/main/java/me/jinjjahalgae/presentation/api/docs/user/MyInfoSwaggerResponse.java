package me.jinjjahalgae.presentation.api.docs.user;

import me.jinjjahalgae.domain.user.dto.MyInfoResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class MyInfoSwaggerResponse extends CommonResponse<MyInfoResponse> {
    public MyInfoSwaggerResponse(MyInfoResponse result) {
        super(result);
    }
} 