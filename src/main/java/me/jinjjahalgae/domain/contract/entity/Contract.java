package me.jinjjahalgae.domain.contract.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //계약 id

    private Long userId; // 유저 id

    private String uuid; //계약 고유 uuid

    private LocalDate startDate; //계약 시작일

    private LocalDate endDate; //계약 종료일

    private String title; //목표 제목

    private String goal; //성공 목표

    private String penalty; //벌칙

    private String reward; //보상

    private int life; //실패 가능 횟수

    private int proofPerWeek; //주간 인증 횟수

    private int totalProof; //총 인증 횟수

    private int currentProof; //현재 인증 횟수

    private int totalSupervisor; //감독자 수

    @Enumerated(EnumType.STRING)
    private ContractStatus status; //계약 상태

    @Enumerated(EnumType.STRING)
    private ContractType type; //계약서 템플릿 타입
}
