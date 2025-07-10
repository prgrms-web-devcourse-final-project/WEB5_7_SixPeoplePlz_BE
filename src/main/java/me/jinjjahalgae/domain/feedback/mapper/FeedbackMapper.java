package me.jinjjahalgae.domain.feedback.mapper;

import me.jinjjahalgae.domain.feedback.dto.FeedbackResponse;
import me.jinjjahalgae.domain.feedback.entity.Feedback;

public class FeedbackMapper {

    public static FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getCreatedAt(),
                feedback.getStatus(),
                feedback.getComment()
        );
    }
}
