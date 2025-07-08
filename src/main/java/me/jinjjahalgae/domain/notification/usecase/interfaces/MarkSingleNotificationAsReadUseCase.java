package me.jinjjahalgae.domain.notification.usecase.interfaces;

import me.jinjjahalgae.domain.common.UseCase;

/**
 * 개별 알림 읽음 처리
 */
public interface MarkSingleNotificationAsReadUseCase extends UseCase<Long, Long> {
    @Override
    Long execute(Long notificationId);
}
