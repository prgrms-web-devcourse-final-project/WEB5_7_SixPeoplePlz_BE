package me.jinjjahalgae.domain.feedback.repository;

import me.jinjjahalgae.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
