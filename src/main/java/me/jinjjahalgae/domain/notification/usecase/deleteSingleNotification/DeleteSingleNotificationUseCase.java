package me.jinjjahalgae.domain.notification.usecase.deleteSingleNotification;

/**
 * 알림 개별 삭제
 * 삭제할 알림id를 받고, 삭제된 알림의 id를 반환
 */
public interface DeleteSingleNotificationUseCase {
    void execute(Long notificationId);
}
