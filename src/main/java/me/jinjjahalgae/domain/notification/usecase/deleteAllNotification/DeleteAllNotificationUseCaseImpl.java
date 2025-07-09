package me.jinjjahalgae.domain.notification.usecase.deleteAllNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteAllNotificationUseCaseImpl implements DeleteAllNotificationUseCase {

    private final NotificationRepository notificationRepository;

    /**
     * userid에 해당하는 알림들을 일괄 삭제함
     * @param userId 알림 일괄 삭제할 userid
     * @return 삭제된 알림 개수
     */
    @Override
    @Transactional // 트랜잭션 생성
    public Long execute(Long userId) {

        return notificationRepository.deleteAllByUserId(userId);
    }
}
