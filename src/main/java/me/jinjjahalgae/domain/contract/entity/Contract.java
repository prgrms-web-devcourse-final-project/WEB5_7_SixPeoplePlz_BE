package me.jinjjahalgae.domain.contract.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.common.BaseEntity;
import me.jinjjahalgae.domain.contract.enums.ContractStatus;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.participation.enums.Role;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 계약과 유저 : 관계 매핑
 * 계약서 정보가 필요한 상황에서는
 * 유저의 이름이나 닉네임이 항상 필요할 것으로 생각됨. (아마 이름..)
 * 지연 로딩으로 설정하되 조회 시에는 fetch join 으로 N+1 문제를 방지
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //계약 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 유저 조인

    private String uuid; //계약 고유 uuid

    private LocalDateTime startDate; //계약 시작일

    private LocalDateTime endDate; //계약 종료일

    private String title; //목표 제목

    private String goal; //성공 목표

    private String penalty; //벌칙

    private String reward; //보상

    private int life; //실패 가능 횟수

    private int proofPerWeek; //주간 인증 횟수

    private int totalProof; //총 인증 횟수

    private int currentProof; //현재 인증 횟수

    private int totalSupervisor; //감독자 수

    private boolean oneOff; //단발성 여부

    @Enumerated(EnumType.STRING)
    private ContractStatus status; //계약 상태

    @Enumerated(EnumType.STRING)
    private ContractType type; //계약서 템플릿 타입

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

    @Builder
    private Contract(User user, LocalDateTime startDate, LocalDateTime endDate, String title, String goal, String penalty, String reward, int life,
                    int proofPerWeek, boolean oneOff, ContractType type) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.goal = goal;
        this.penalty = penalty;
        this.reward = reward;
        this.life = life;
        this.proofPerWeek = proofPerWeek;
        this.oneOff = oneOff;
        this.type = type;
    }

    public void initialize() {
        this.uuid = UUID.randomUUID().toString();
        this.totalProof = calculateTotalProof(this.startDate, this.endDate, this.proofPerWeek);
        this.currentProof = 0;
        this.totalSupervisor = 0;
        this.status = ContractStatus.PENDING;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
        participation.setContract(this);
    }

    private int calculateTotalProof(LocalDateTime startDate, LocalDateTime endDate, int proofPerWeek) {
        if (oneOff) {
            return 1;
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalWeeks = (totalDays + 6) / 7; // 프론트에서 날짜 선택을 막으면 몇주인지 올림하여 계산하기만 하면 되기 때문에 6을 더해서 항상 올림 처리
        return (int) (totalWeeks * proofPerWeek);
    }

    public double calculateAchievementPercent() {
        return ( (double) currentProof / totalProof * 100);
    }

    public double calculatePeriodPercent() {

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            return 0.0; // 시작 전이면 0%
        }
        if (now.isAfter(endDate)) {
            return 100.0; // 종료 후면 100%
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long passedDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, now) +1;

        return ( (double) passedDays / totalDays * 100);
    }

    // 참여 정보를 제거하는 메서드
    public void removeParticipation(Participation participation) {
        this.participations.remove(participation);
    }

    // 감독자 수를 감소시키는 메서드
    public void decrementTotalSupervisor() {
        if (this.totalSupervisor > 0) {
            this.totalSupervisor--;
        }
    }

    //감독자가 이미 있는지 검증 (수정은 감독자가 없어야 가능)
    public void validateUpdatable() {
        boolean hasSignedSupervisor = this.participations.stream()
                .anyMatch(participation -> participation.getRole() == Role.SUPERVISOR);
        if (hasSignedSupervisor) {
            throw ErrorCode.CONTRACT_ALREADY_SIGNED.domainException("감독자가 서명한 계약은 수정할 수 없습니다.");
        }
    }

    //계약 수정
    public void updateContract(String title, String goal, String penalty, String reward,
                               int life, int proofPerWeek, boolean oneOff,
                               LocalDateTime startDate, LocalDateTime endDate, ContractType type) {
        this.title = title;
        this.goal = goal;
        this.penalty = penalty;
        this.reward = reward;
        this.life = life;
        this.proofPerWeek = proofPerWeek;
        this.oneOff = oneOff;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;

        // 수정 시 총 인증 횟수도 다시 계산
        this.totalProof = calculateTotalProof(startDate, endDate, proofPerWeek);
    }

    // 총 감독자 수를 받아 계약을 시작
    public void start(int finalSupervisorCount) {
        this.totalSupervisor = finalSupervisorCount;
        this.status = ContractStatus.IN_PROGRESS;
    }
}