package me.jinjjahalgae.domain.notification.usecase.delete.all;

/**
 * 알림 목록 전체 삭제
 * 알림을 삭제할 userid를 받고, 삭제된 알림의 수를 반환
 */
public interface DeleteAllNotificationUseCase {
    void execute(Long userId);
}
