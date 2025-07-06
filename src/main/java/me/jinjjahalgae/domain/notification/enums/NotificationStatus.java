package me.jinjjahalgae.domain.notification.enums;

public enum NotificationStatus {
    SUPERVISOR_ADDED, // 감독자 추가
    SUPERVISOR_WITHDRAWN, // 감독자 포기

    CONTRACT_STARTED, // 계약 시작
    CONTRACT_ENDED, // 계약 종료

    FEEDBACK_ADDED, // 피드백 등록

    PROOF_ADDED, // 인증 등록
    PROOF_ACCEPTED, // 인증 승인
    PROOF_REJECTED, // 인증 거절

    // 재인증 등록... API에서는 again으로 썼던데 용어 이걸로 해도 괜찮을지..
    RECERTIFICATION_ADDED
}
