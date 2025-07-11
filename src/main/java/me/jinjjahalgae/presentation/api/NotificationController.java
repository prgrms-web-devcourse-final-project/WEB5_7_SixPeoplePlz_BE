package me.jinjjahalgae.presentation.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.notification.usecase.delete.all.DeleteAllNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.delete.single.DeleteSingleNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.get.all.GetAllNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.get.all.dto.NotificationGetResponse;
import me.jinjjahalgae.domain.notification.usecase.get.unreadcount.GetCountUnreadNotificationUseCase;
import me.jinjjahalgae.domain.notification.usecase.update.asread.UpdateSingleNotificationAsReadUseCase;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.notification.NotificationControllerDocs;
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
public class NotificationController implements NotificationControllerDocs {

    private final GetAllNotificationUseCase getAllNotification;
    private final GetCountUnreadNotificationUseCase countUnreadNotificationByUserId;
    private final DeleteAllNotificationUseCase deleteAllNotification;
    private final DeleteSingleNotificationUseCase deleteSingleNotification;
    private final UpdateSingleNotificationAsReadUseCase markSingleNotificationAsRead;

    /**
     * 현재 로그인한 사용자의 모든 알림 목록 조회
     * @param principal
     * @param pageable
     * @return
     */
    @GetMapping
    public CommonResponse<Page<NotificationGetResponse>> getAllNotifications(
            @AuthenticationPrincipal CustomJwtPrincipal principal,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
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
     * @return 없음
     */
    @DeleteMapping
    public CommonResponse<Void> deleteAllNotification(@AuthenticationPrincipal CustomJwtPrincipal principal) {
        deleteAllNotification.execute(principal.getUserId());
        return CommonResponse.success();
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
