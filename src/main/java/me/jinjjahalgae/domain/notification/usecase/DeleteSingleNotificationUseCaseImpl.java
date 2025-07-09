package me.jinjjahalgae.domain.notification.usecase;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.domain.notification.usecase.interfaces.DeleteSingleNotificationUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteSingleNotificationUseCaseImpl implements DeleteSingleNotificationUseCase {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional // 트랜잭션 생성
    public void execute(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
