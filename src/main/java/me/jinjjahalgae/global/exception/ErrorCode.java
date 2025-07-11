package me.jinjjahalgae.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 시스템
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),

    // auth 관련
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "유효하지 않은 provider입니다."),

    // 인증 관련
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    // 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),
    DUPLICATE_AUTH_SOCIAL(HttpStatus.CONFLICT,"이미 연결된 소셜 로그인 플랫폼입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST,"존재하지 않는 토큰입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 계약 관련
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "계약에 대한 접근 권한이 없습니다."),
    CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계약입니다."),
    CONTRACT_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "이미 활성화된 계약입니다."),
    CONTRACT_ALREADY_START(HttpStatus.CONFLICT, "이미 시작된 계약은 수정할 수 없습니다."),
    CONTRACT_ALREADY_SIGNED(HttpStatus.CONFLICT, "감독자가 서명한 계약은 수정할 수 없습니다."),
    CONTRACT_STATUS_CONFLICT(HttpStatus.CONFLICT, "계약 상태 변경 중 충돌이 발생했습니다."),
    CONTRACT_NOT_IN_PROGRESS(HttpStatus.CONFLICT,"진행 중인 계약만 포기할 수 있습니다."),
    CONTRACT_NOT_PENDING(HttpStatus.CONFLICT,"시작 전인 계약만 포기할 수 있습니다."),
    CANNOT_ABANDON_STARTED_CONTRACT(HttpStatus.CONFLICT, "이미 시작된 계약은 포기할 수 없습니다."),
    CANNOT_WITHDRAW_PARTICIPATION_AFTER_START(HttpStatus.CONFLICT, "계약이 시작히기 전에만 계약 감독을 철회할 수 있습니다."),
    CANNOT_ABANDON_PARTICIPATION_UNLESS_IN_PROGRESS(HttpStatus.CONFLICT, "계약 진행중에만 감독을 중도 포기할 수 있습니다."),

    // 인증 관련
    CONTRACT_NOT_STARTED(HttpStatus.BAD_REQUEST, "계약 시작 전에는 인증을 생성할 수 없습니다."),
    PROOF_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인증을 찾을 수 없습니다."),
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "최소 1장 이상의 이미지가 필요합니다."),
    INVALID_YEAR_MONTH(HttpStatus.BAD_REQUEST, "유효하지 않은 년, 월입니다."),
    REPROOF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "재인증이 불가능합니다."),
    PROOF_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 날짜에 이미 인증이 존재합니다."),
    REPROOF_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 날짜에 이미 재인증이 존재합니다."),

    // 초대 관련
    SUPERVISOR_ALREADY_FULL(HttpStatus.CONFLICT, "이미 5명의 감독자가 참여했습니다."),
    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않거나 만료된 초대입니다."),
    INVALID_INVITE_PASSWORD(HttpStatus.UNAUTHORIZED, "초대 비밀번호가 일치하지 않습니다."),
    INVITE_ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "이미 참여한 계약입니다."),
    CONTRACTOR_PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "계약자의 정보가 없는 계약입니다."),
    SUPERVISOR_PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 감독자의 정보가 없는 계약입니다."),
    CANNOT_CREATE_INVITE_AFTER_START(HttpStatus.CONFLICT, "계약이 시작히기 전에만 초대 코드를 생성할 수 있습니다."),

    // 알림 관련
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림 타입입니다."),
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 알림 타입입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public AppException serviceException() {
        return new AppException(this, ErrorType.SERVICE);
    }

    public AppException serviceException(String detail) {
        return new AppException(this, detail, ErrorType.SERVICE);
    }

    public AppException domainException() {return new AppException(this, ErrorType.DOMAIN);}

    public AppException domainException(String detail) {
        return new AppException(this, detail, ErrorType.DOMAIN);
    }

}
