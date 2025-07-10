package me.jinjjahalgae.domain.feedback.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.common.BaseEntity;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.proof.entities.Proof;

@Entity
@Getter
@NoArgsConstructor
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 피드백 id

    private Long userId;        // 유저 id (FK)

    private String comment;     // 코멘트

    private FeedbackStatus status;  // 피드백 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proof_id")
    private Proof proof;

    @Builder
    public Feedback(Long userId, String comment, FeedbackStatus status, Proof proof) {
        this.userId = userId;
        this.comment = comment;
        this.status = status;
        this.proof = proof;
    }

    // 연관관계 편의 메서드
    public void assignProof(Proof proof) {
        this.proof = proof;
    }
}
