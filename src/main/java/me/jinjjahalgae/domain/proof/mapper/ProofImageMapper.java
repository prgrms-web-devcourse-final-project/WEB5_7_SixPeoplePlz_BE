package me.jinjjahalgae.domain.proof.mapper;

import me.jinjjahalgae.domain.proof.entities.ProofImage;

public class ProofImageMapper {

    public static ProofImage toEntity(String imageKey, int index) {
        return ProofImage.builder()
                .imageKey(imageKey)
                .index(index)
                .build();
    }
}
