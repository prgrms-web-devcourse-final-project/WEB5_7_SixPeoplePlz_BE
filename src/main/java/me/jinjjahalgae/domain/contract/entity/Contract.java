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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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

    private Instant startDate; //계약 시작일

    private Instant endDate; //계약 종료일

    private String title; //목표 제목

    private String goal; //성공 목표

    private String penalty; //벌칙

    private String reward; //보상

    private int totalProof; //총 인증 횟수

    private int currentProof; //현재 인증 횟수

    private int totalSupervisor; //감독자 수

    private boolean oneOff; //단발성 여부

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private ContractStatus status; //계약 상태

    @Enumerated(EnumType.STRING)
    private ContractType type; //계약서 템플릿 타입

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

    @Builder
    private Contract(User user, Instant startDate, Instant endDate, String title, String goal, String penalty, String reward, int totalProof, boolean oneOff, ContractType type) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.goal = goal;
        this.penalty = penalty;
        this.reward = reward;
        this.totalProof = totalProof;
        this.oneOff = oneOff;
        this.type = type;
    }

    public void initialize() {
        this.uuid = UUID.randomUUID().toString();
        this.currentProof = 0;
        this.totalSupervisor = 0;
        this.status = ContractStatus.PENDING;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
        participation.setContract(this);
    }

    public String calculateAchievementRatio() {
        return currentProof + "/" + totalProof;
    }

    public double calculateAchievementPercent() {
        return ( (double) currentProof / totalProof * 100);
    }

    public String calculatePeriodRatio() {
        long totalDays = getTotalDays();
        long passedDays = getPassedDays();
        return passedDays + "/" + totalDays;
    }

    public double calculatePeriodPercent() {
        if (status == ContractStatus.COMPLETED ||
                status == ContractStatus.FAILED ||
                status == ContractStatus.ABANDONED ||
                status == ContractStatus.WAIT_RESULT) {
            return 100.0;
        }

        if (status == ContractStatus.IN_PROGRESS) {
            LocalDateTime startLdt = LocalDateTime.ofInstant(this.startDate, ZoneOffset.UTC);
            LocalDateTime endLdt = LocalDateTime.ofInstant(this.endDate, ZoneOffset.UTC);
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

            if (startLdt.isAfter(endLdt)) {
                return 0.0;
            }

            if (now.isBefore(startLdt)) {
                return 0.0;
            }

            if (now.isAfter(endLdt)) {
                return 100.0;
            }

            long totalDuration = ChronoUnit.MILLIS.between(startLdt, endLdt);
            if (totalDuration <= 0) {
                return 100.0;
            }

            long elapsedDuration = ChronoUnit.MILLIS.between(startLdt, now);

            return ((double) elapsedDuration / totalDuration) * 100.0;
        }

        return 0.0;
    }

    private long getTotalDays() {
        LocalDateTime start = LocalDateTime.ofInstant(startDate, ZoneOffset.UTC);
        LocalDateTime end = LocalDateTime.ofInstant(endDate, ZoneOffset.UTC);
        return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    private long getPassedDays() {
        Instant now = Instant.now();
        LocalDateTime nowLdt = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        LocalDateTime start = LocalDateTime.ofInstant(startDate, ZoneOffset.UTC);
        LocalDateTime end = LocalDateTime.ofInstant(endDate, ZoneOffset.UTC);
        if (nowLdt.isBefore(start)) return 0;
        if (nowLdt.isAfter(end)) return getTotalDays();
        return java.time.temporal.ChronoUnit.DAYS.between(start, nowLdt) + 1;
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

    // 기존 계약자 서명 삭제
    public void removeContractorSignature() {
        this.participations.removeIf(participation ->
                participation.getRole() == Role.CONTRACTOR);
    }

    // 새로운 계약자 서명 추가
    public void addContractorSignature(Participation newSignature) {
        this.participations.add(newSignature);
    }

    //감독자가 이미 있는지 검증 (수정은 감독자가 없어야 가능)
    public void validateUpdatable() {

        if(this.status != ContractStatus.PENDING) {
            throw ErrorCode.CONTRACT_STATUS_INVALID.domainException("계약 수정은 대기 상태에서만 가능합니다.");
        }
        boolean hasSignedSupervisor = this.participations.stream()
                .anyMatch(participation -> participation.getRole() == Role.SUPERVISOR);
        if (hasSignedSupervisor) {
            throw ErrorCode.CONTRACT_ALREADY_SIGNED.domainException("감독자가 서명한 계약은 수정할 수 없습니다.");
        }
    }

    //계약 수정
    public void update(String title, String goal, String penalty, String reward,
                       int totalProof, boolean oneOff,
                       Instant startDate, Instant endDate, ContractType type) {

        this.title = title;
        this.goal = goal;
        this.penalty = penalty;
        this.reward = reward;
        this.totalProof = totalProof;
        this.oneOff = oneOff;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    // 총 감독자 수를 받아 계약을 시작
    public void start(int finalSupervisorCount) {
        this.totalSupervisor = finalSupervisorCount;
        this.status = ContractStatus.IN_PROGRESS;
    }
    // 단발성 계약 시작 (시작/종료 시간 갱신)
    public void startOneOffContract(int finalSupervisorCount) {
        this.totalSupervisor = finalSupervisorCount;
        this.status = ContractStatus.IN_PROGRESS;
        this.startDate = Instant.now();
        this.endDate = this.startDate.plus(24, java.time.temporal.ChronoUnit.HOURS);
    }

    // 계약 성공 처리
    public void complete() {
        this.status = ContractStatus.COMPLETED;
    }

    // 계약 실패 처리
    public void fail() {
        this.status = ContractStatus.FAILED;
    }
  
    //권한 검증 (계약자인가?)
    public void validateContractor(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw ErrorCode.ACCESS_DENIED.domainException("계약에 대한 접근 권한이 없습니다.");
        }
    }

    //계약 상태 변경 (중도 포기)
    public void withdraw() {
        if (this.status != ContractStatus.IN_PROGRESS) {
            throw ErrorCode.CONTRACT_NOT_IN_PROGRESS.domainException("진행 중인 계약만 포기할 수 있습니다.");
        }
        this.status = ContractStatus.ABANDONED;
    }

    //계약 삭제 (시작 전 포기)
    public void cancel() {
        if (this.status != ContractStatus.PENDING) {
            throw ErrorCode.CONTRACT_NOT_PENDING.domainException("시작 전인 계약만 포기할 수 있습니다.");
        }
    }

    // 현재 인증 횟수 증가
    public void incrementCurrentProof() {
        this.currentProof++;
    }

    // 서명 전 계약 시작 여부 확인
    public void isPending() {
        if (this.status != ContractStatus.PENDING) {
            throw ErrorCode.CANNOT_PARTICIPATE_AFTER_START.domainException("시작 전인 계약만 서명할 수 있습니다.");
        }
    }

    // 계약 상태가 진행중인지 검증
    public boolean isInProgress() {
        return this.status == ContractStatus.IN_PROGRESS;
    }

    //총 실행 횟수 검증
    public void validateTotalProof() {
        long contractDays = getTotalDays();

        if (this.oneOff && this.totalProof != 1) {
            throw ErrorCode.INVALID_ONE_OFF_TOTAL_PROOF.domainException("단발성 계약의 실행 횟수는 반드시 1이여야 합니다.");
        }

        if (this.totalProof > contractDays) {
            throw ErrorCode.INVALID_TOTAL_PROOF.domainException("계약 실행 횟수는 계약 기간 일을 넘길 수 없습니다.");
        }
    }

    public void validateDates() {
        if (startDate.isAfter(endDate)) {
            throw ErrorCode.INVALID_CONTRACT_DATES.domainException("계약 종료일은 시작일보다 늦어야 합니다.");
        }
    }

    // 계약 상태가 결과대기인지 검증
    public boolean isWaitResult() {
        return this.status == ContractStatus.WAIT_RESULT;
    }
}