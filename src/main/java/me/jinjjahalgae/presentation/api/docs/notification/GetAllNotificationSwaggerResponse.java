package me.jinjjahalgae.presentation.api.docs.notification;

import me.jinjjahalgae.domain.notification.usecase.get.all.dto.NotificationGetResponse;
import me.jinjjahalgae.global.common.CommonResponse;

public class GetAllNotificationSwaggerResponse extends CommonResponse<NotificationGetResponse> {
    public GetAllNotificationSwaggerResponse(NotificationGetResponse result) { super(result);}
}

