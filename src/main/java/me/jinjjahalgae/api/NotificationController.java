package me.jinjjahalgae.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.notification.dto.NotificationGetResponse;
import me.jinjjahalgae.domain.notification.usecase.interfaces.*;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetAllNotificationUseCase getAllNotification;
    private final CountUnreadNotificationByUserIdUseCase countUnreadNotificationByUserId;
    private final DeleteAllNotificationUseCase deleteAllNotification;
    private final DeleteSingleNotificationUseCase deleteSingleNotification;
    private final MarkSingleNotificationAsReadUseCase markSingleNotificationAsRead;

    /**
     * 현재 로그인한 사용자의 모든 알림 목록 조회
     * @param principal
     * @param pageable
     * @return
     */
    @GetMapping
    public CommonResponse<Page<NotificationGetResponse>> getAllNotifications(
            @AuthenticationPrincipal CustomJwtPrincipal principal,
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return CommonResponse.success(getAllNotification.execute(principal.getUserId(), pageable));
    }

    /**
     * 현재 로그인한 사용자가 읽지 않은 알림 개수 세기
     * @param principal
     * @return
     */
    @GetMapping("/unread")
    public CommonResponse<Long> countUnreadNotificationByUserId(
            @AuthenticationPrincipal CustomJwtPrincipal principal
    ) {
        return CommonResponse.success(countUnreadNotificationByUserId.execute(principal.getUserId()));
    }

    /**
     * 현재 로그인한 사용자의 알림을 모두 삭제
     * @param principal
     * @return 삭제한 알림의 개수
     */
    @DeleteMapping
    public CommonResponse<Long> deleteAllNotification(@AuthenticationPrincipal CustomJwtPrincipal principal) {
        return CommonResponse.success(deleteAllNotification.execute(principal.getUserId()));
    }

    /**
     * 주어진 알림id에 해당하는 알림을 삭제
     * @param notificationId
     * @return 없음
     */
    @DeleteMapping("/{notificationId}")
    public CommonResponse<Void> deleteSingleNotification(@PathVariable Long notificationId) {
        deleteSingleNotification.execute(notificationId);
        return CommonResponse.success();
    }

    /**
     * 주어진 알림id에 해당하는 알림을 읽음 상태로 설정
     * @param notificationId
     * @return 없음
     */
    @PatchMapping("/{notificationId}/read")
    public CommonResponse<Void> markSingleNotificationAsRead(@PathVariable Long notificationId) {
        markSingleNotificationAsRead.execute(notificationId);
        return CommonResponse.success();
    }


}
