package me.jinjjahalgae.domain.notification.usecase.get.all;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jinjjahalgae.domain.notification.repository.NotificationRepository;
import me.jinjjahalgae.domain.notification.usecase.get.all.dto.NotificationGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllNotificationUseCaseImpl implements GetAllNotificationUseCase {

    private final NotificationRepository repository;

    /**
     * 알림 목록 조회
     * @param userId 알림 목록을 조회할 userId
     * @param pageable 조회할 알림 목록 페이지 정보
     * @return NotificationGetResponse의 Page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationGetResponse> execute(Long userId, Pageable pageable) {

        return repository.findAllByUserId(userId, pageable)
                .map(NotificationGetResponse::from);

    }
}
