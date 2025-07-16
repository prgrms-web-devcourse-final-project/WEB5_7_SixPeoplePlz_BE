package me.jinjjahalgae.domain.notification.usecase.delete.single;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteSingleNotificationUseCaseImpl implements DeleteSingleNotificationUseCase {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional // 트랜잭션 생성
    public void execute(Long notificationId) {
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> ErrorCode.NOTIFICATION_NOT_FOUND.domainException("존재하지 않는 알림id : " + notificationId));
        notificationRepository.deleteById(notificationId);
    }
}
