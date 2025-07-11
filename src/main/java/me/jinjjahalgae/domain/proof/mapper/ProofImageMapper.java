package me.jinjjahalgae.domain.proof.mapper;

import me.jinjjahalgae.domain.proof.entities.ProofImage;

import java.util.Comparator;
import java.util.List;

public class ProofImageMapper {

    public static ProofImage toEntity(String imageKey, int index) {
        return ProofImage.builder()
                .imageKey(imageKey)
                .index(index)
                .build();
    }

    public static List<String> toListResponse(List<ProofImage> proofImages) {
        return proofImages.stream()
                .sorted(Comparator.comparingInt(ProofImage::getIndex))
                .map(ProofImage::getImageKey)
                .toList();
    }
}
