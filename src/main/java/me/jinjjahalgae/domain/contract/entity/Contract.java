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
import me.jinjjahalgae.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 계약과 유저 : 관계 매핑
 * 계약서 정보가 필요한 상황에서는
 * 유저의 이름이나 닉네임이 항상 필요할 것으로 생각됨.
 * 때문에 관계 매핑을 통해 조인.
 *
 * 외래키로 단순하게 연결할 경우 N+1 문제를 방지할 수 있지만
 * 닉네임이 필요한 순간마다
 * 계속 userRepository를 사용하는 것 보다는
 * contract.getUser.getNickname 한 줄로 사용(한줄로 처리)
 *
 * 또한 지연 로딩을 사용하여 N+1 문제 방지
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
}