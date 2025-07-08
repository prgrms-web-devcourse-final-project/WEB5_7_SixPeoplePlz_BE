package me.jinjjahalgae.domain.notification.usecase.interfaces;

import me.jinjjahalgae.domain.common.UseCase;
import me.jinjjahalgae.domain.notification.dto.NotificationCreateRequest;
import me.jinjjahalgae.domain.notification.dto.NotificationCreateResponse;
import me.jinjjahalgae.domain.notification.dto.NotificationResponse;

/**
 * REQ : NotificationCreateRequest
 * RES : 알림id와 생성된 메세지를 담은 NotificationCreateResponse
 */
public interface CreateNotificationUseCase extends UseCase<NotificationCreateRequest, NotificationCreateResponse> {

    NotificationCreateResponse execute(NotificationCreateRequest request);
}
