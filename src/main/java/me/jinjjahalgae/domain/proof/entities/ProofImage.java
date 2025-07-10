package me.jinjjahalgae.domain.proof.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProofImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 인증 사진 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proof_id")
    private Proof proof;    // 인증 (FK)

    private String imageKey;    // 이미지 키

    @Column(name = "`index`", nullable = false)
    private int index;  // 인덱스

    @Builder
    public ProofImage(Long id, Proof proof, String imageKey, int index) {
        this.id = id;
        this.proof = proof;
        this.imageKey = imageKey;
        this.index = index;
    }

    // 연관관계 편의 메서드
    public void assignProof(Proof proof) {
        this.proof = proof;
    }
}
