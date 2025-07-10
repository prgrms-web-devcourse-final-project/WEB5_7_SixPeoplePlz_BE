package me.jinjjahalgae.domain.notification.usecase.countUnreadNotificationByUserId;

public interface CountUnreadNotificationByUserIdUseCase {

    Long execute(Long userId);
}
