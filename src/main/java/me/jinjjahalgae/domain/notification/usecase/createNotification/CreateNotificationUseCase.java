package me.jinjjahalgae.domain.notification.usecase.createNotification;

/**
 * 알림 생성 usecase (백엔드에서만 쓰일 예정)
 * REQ : NotificationCreateRequest
 * RES : 알림id와 생성된 메세지를 담은 NotificationCreateResponse
 */
public interface CreateNotificationUseCase {

    void execute(NotificationCreateRequest request);
}
