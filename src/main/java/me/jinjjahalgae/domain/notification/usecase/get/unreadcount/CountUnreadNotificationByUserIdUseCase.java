package me.jinjjahalgae.domain.notification.usecase.get.unreadcount;

public interface CountUnreadNotificationByUserIdUseCase {

    Long execute(Long userId);
}
