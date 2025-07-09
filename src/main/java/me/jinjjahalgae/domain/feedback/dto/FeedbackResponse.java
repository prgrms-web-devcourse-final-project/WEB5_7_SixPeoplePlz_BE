package me.jinjjahalgae.domain.feedback.dto;

import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;

import java.time.LocalDateTime;

public record FeedbackResponse(
        LocalDateTime createdAt,
        FeedbackStatus status,
        String comment
) {
}
