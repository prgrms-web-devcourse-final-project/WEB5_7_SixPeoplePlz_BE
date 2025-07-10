package me.jinjjahalgae.domain.proof.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.common.BaseEntity;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 계약 ID와 재인증 ID는 관계 매핑 대신 키 참조로 설정
 * 인증이라는 정보를 가져올 때 계약의 정보를 같이 가져올 상황이 없을 거 같음
 *  - 계약 내부에 인증이 존재하니 인증 정보에선 계약 ID만 같이 넘겨줘도 충분하다고 생각해
 *    객체 그래프 탐색(관계 매핑)을 사용할 일이 없을 것으로 판단
 *  - 초안(인증과 재인증 정보를 하나의 페이지에서 모두 출력)의 형태였다면 관계 매핑을 통해
 *    객체 그래프 탐색이 효율적이었을 수 있으나 재인증이 별도의 카드로 분리되어
 *    인증과 재인증의 정보를 한 번에 가져올 일이 없다고 판단하여 키 참조로 설정
 *    인증 상세보기 -> 인증 id로 조회, 재인증 상세보기 -> 재인증 id를 통해 조회
 *    두 인증 정보를 하나의 응답으로 보낼 일이 없을 것으로 예상?
 *
 * 인증 사진에 대해서 관계 매핑 사용
 *  - 인증 상세보기에선 무조건 인증 사진 정보를 같이 가져와야 함
 *    관계 매핑으로 편하게 탐색 가능
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proof extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 인증 ID

    private String comment;     //코멘트

    private int checkedSupervisors; // 확인한 감독자 수

    private int totalSupervisors;   // 총 감독자 수

    @Enumerated(EnumType.STRING)
    private ProofStatus status;  // 인증 상태

    private Long contractId;    // 계약 ID (FK)

    private Long proofId;   // 원본 인증 ID (FK)

    @OneToMany(mappedBy = "proof", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProofImage> proofImages = new ArrayList<>();

    @OneToMany(mappedBy = "proof", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    @Builder
    public Proof(String comment, int checkedSupervisors, int totalSupervisors, ProofStatus status, Long contractId, Long proofId) {
        this.comment = comment;
        this.checkedSupervisors = checkedSupervisors;
        this.totalSupervisors = totalSupervisors;
        this.status = status;
        this.contractId = contractId;
        this.proofId = proofId;
    }

    // 연관관계 편의 메서드
    public void addProofImage(ProofImage proofImage) {
        proofImages.add(proofImage);
        proofImage.assignProof(this);
    }

    public void addFeedback(Feedback feedback) {
        feedbacks.add(feedback);
        feedback.assignProof(this);
    }
}
