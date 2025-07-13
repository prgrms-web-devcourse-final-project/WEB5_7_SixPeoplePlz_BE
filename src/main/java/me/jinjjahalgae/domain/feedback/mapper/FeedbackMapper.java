package me.jinjjahalgae.domain.feedback.mapper;

import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.feedback.usecase.common.dto.FeedbackResponse;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.usecase.create.dto.CreateFeedbackRequest;
import me.jinjjahalgae.domain.proof.entities.Proof;

public class FeedbackMapper {
    public static Feedback toEntity(Long userId, CreateFeedbackRequest req, Proof proof) {
        FeedbackStatus status = FeedbackStatus.valueOf(req.status());

        return Feedback.builder()
                .userId(userId)
                .comment(req.comment())
                .status(status)
                .proof(proof)
                .build();
    }

    public static FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getCreatedAt(),
                feedback.getStatus(),
                feedback.getComment()
        );
    }
}
