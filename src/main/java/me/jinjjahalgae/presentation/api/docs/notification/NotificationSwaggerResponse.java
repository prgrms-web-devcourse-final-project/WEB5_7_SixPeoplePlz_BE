package me.jinjjahalgae.presentation.api.docs.notification;

import me.jinjjahalgae.domain.notification.dto.NotificationGetResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class NotificationSwaggerResponse extends CommonResponse<NotificationGetResponse> {
    public NotificationSwaggerResponse(NotificationGetResponse result) { super(result);}
}

