package me.jinjjahalgae.domain.contract.enums;

public enum ContractStatus {
    PENDING, //시작 전
    IN_PROGRESS, //진행 중
    COMPLETED, //이행 성공
    FAILED, //이행 실패
    ABANDONED //중간 포기
}
