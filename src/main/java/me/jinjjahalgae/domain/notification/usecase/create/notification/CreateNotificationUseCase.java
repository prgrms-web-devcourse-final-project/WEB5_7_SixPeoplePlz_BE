package me.jinjjahalgae.domain.notification.usecase.create.notification;

/**
 * 알림 생성 usecase (백엔드에서만 쓰일 예정)
 * REQ : NotificationCreateRequest
 * RES : 반환값 없음
 */
public interface CreateNotificationUseCase {

    void execute(NotificationCreateRequest request);
}
