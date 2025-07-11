package me.jinjjahalgae.domain.notification.enums;

public enum NotificationType {
    SUPERVISOR_ADDED, // 감독자 추가
    SUPERVISOR_WITHDRAWN, // 감독자 포기(탈퇴)

    CONTRACT_STARTED, // 계약 시작
    CONTRACT_AUTO_DELETED, // 계약 자동 삭제 (감독자 부족)
    CONTRACT_ENDED_FAIL, // 계약 성공 종료
    CONTRACT_ENDED_SUCCESS, // 계약 실패 종료

    PROOF_ADDED, // 인증 등록
    PROOF_ACCEPTED, // 인증 승인
    PROOF_REJECTED, // 인증 거절

    FEEDBACK_ADDED, // 피드백 등록

    REPROOF_ADDED // 재인증 등록
}
