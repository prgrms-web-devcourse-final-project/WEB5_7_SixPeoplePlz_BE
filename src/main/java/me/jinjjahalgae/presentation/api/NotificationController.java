package me.jinjjahalgae.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.notification.usecase.interfaces.MarkSingleNotificationAsReadUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final MarkSingleNotificationAsReadUseCase markSingleNotificationAsReadUseCase;

//    @PatchMapping("/{notificationId}/read")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public CommonResponse
}
