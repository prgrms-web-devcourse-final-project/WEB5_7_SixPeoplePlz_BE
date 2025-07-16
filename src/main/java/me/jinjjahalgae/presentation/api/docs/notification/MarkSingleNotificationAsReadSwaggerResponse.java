package me.jinjjahalgae.presentation.api.docs.notification;

import me.jinjjahalgae.global.common.CommonResponse;

public class MarkSingleNotificationAsReadSwaggerResponse extends CommonResponse<Long> {
    public MarkSingleNotificationAsReadSwaggerResponse(Long notificationId) { super(notificationId); }
}

