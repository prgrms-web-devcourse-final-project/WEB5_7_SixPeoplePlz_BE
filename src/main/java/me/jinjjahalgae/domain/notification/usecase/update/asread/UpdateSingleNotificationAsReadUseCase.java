package me.jinjjahalgae.domain.notification.usecase.update.asread;

/**
 * 개별 알림 읽음 처리
 * 알림id를 받고, 상태변경된 알림id를 반환한다
 */
public interface UpdateSingleNotificationAsReadUseCase {
    Long execute(Long notificationId);
}
