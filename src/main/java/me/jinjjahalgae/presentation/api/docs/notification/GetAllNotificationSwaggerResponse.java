package me.jinjjahalgae.presentation.api.docs.notification;

import me.jinjjahalgae.domain.notification.usecase.get.all.dto.NotificationGetResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import org.springframework.data.domain.Page;


public class GetAllNotificationSwaggerResponse extends CommonResponse<Page<NotificationGetResponse>> {
    public GetAllNotificationSwaggerResponse(Page<NotificationGetResponse> result) { super(result);}
}

