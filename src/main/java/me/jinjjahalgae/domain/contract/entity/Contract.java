package me.jinjjahalgae.domain.contract.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //계약 id

    private Long userId; // 유저 id (FK)

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

    @Builder
    public Contract(Long userId, LocalDate startDate, LocalDate endDate, String title, String goal, String penalty, String reward, int life,
                    int proofPerWeek, ContractType type) {
        this.userId = userId;
        this.uuid = UUID.randomUUID().toString();
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.goal = goal;
        this.penalty = penalty;
        this.reward = reward;
        this.life = life;
        this.proofPerWeek = proofPerWeek;
        this.totalProof = calculateTotalProof(startDate, endDate, proofPerWeek);
        this.currentProof = 0;
        this.totalSupervisor = 0;
        this.status = ContractStatus.PENDING;
        this.type = type;
    }

    private int calculateTotalProof(LocalDate startDate, LocalDate endDate, int proofPerWeek) {
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalWeeks = (totalDays + 6) / 7; // 프론트에서 날짜 선택을 막으면 몇주인지 올림하여 계산하기만 하면 되기 때문에 6을 더해서 항상 올림 처리
        return (int) (totalWeeks * proofPerWeek);
    }
}
