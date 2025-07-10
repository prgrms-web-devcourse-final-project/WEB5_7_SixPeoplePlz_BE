package me.jinjjahalgae.domain.invite.model;

/**
 * 감독자 정보를 담는 record
 * @param supervisorName 감독자 이름
 * @param supervisorSignatureKey 감독자 서명 이미지 키
 */
public record SupervisorResponse(
        String supervisorName,
        String supervisorSignatureKey
) {}
