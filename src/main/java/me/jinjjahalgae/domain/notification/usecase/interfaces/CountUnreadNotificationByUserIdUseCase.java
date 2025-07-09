package me.jinjjahalgae.domain.notification.usecase.interfaces;

public interface CountUnreadNotificationByUserIdUseCase {

    Long execute(Long userId);
}
