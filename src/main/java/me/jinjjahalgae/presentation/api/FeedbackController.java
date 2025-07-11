package me.jinjjahalgae.presentation.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.feedback.usecase.create.CreateFeedbackUseCase;
import me.jinjjahalgae.domain.feedback.usecase.create.dto.CreateFeedbackRequest;
import me.jinjjahalgae.global.common.CommonResponse;
import me.jinjjahalgae.global.security.jwt.CustomJwtPrincipal;
import me.jinjjahalgae.presentation.api.docs.feedback.FeedbackControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController implements FeedbackControllerDocs {
    private final CreateFeedbackUseCase createFeedbackUseCase;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createFeedback(
        @AuthenticationPrincipal CustomJwtPrincipal user,
        @Valid @RequestBody CreateFeedbackRequest request
    ) {
        createFeedbackUseCase.execute(user.getUserId(), request);

        return CommonResponse.success();
    }
} 