package me.jinjjahalgae.domain.signature.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //서명 id

    private Long contractId; //계약 id

    private Long userId; //유저 id

    private String imageKey; //이미지 key (url)

    @Builder
    public Signature(Long contractId, Long userId, String imageKey) {
        this.contractId = contractId;
        this.userId = userId;
        this.imageKey = imageKey;
    }
}
