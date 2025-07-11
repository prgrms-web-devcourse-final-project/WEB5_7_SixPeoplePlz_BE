package me.jinjjahalgae.domain.notification.usecase.get.unreadcount;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCountUnreadNotificationUseCaseImpl implements GetCountUnreadNotificationUseCase {

    private final NotificationRepository notificationRepository;

    /**
     * userid에 해당하는 알림 개수를 조회
     * @param userId 알림 개수를 조회할 userid
     * @return 현재 존재하는 알림 개수
     */
    @Override
    @Transactional(readOnly = true)
    public Long execute(Long userId) {
        return notificationRepository.countUnreadNotificationByUserId(userId);
    }
}
