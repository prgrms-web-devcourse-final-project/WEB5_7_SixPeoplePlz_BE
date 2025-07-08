package me.jinjjahalgae.domain.notification.usecase.interfaces;

import me.jinjjahalgae.domain.notification.dto.NotificationGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 알림 목록 조회
 * 알림 목록을 조회할 userId와 Pageable을 받고
 * Page<NotificationGetResponse>를 반환
 */
public interface GetAllNotificationUseCase {

    Page<NotificationGetResponse> execute(Long userId, Pageable pageable);
}
