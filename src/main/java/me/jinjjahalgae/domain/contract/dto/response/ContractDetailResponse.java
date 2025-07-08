package me.jinjjahalgae.domain.contract.dto.response;

 import java.time.LocalDateTime;
 import java.util.List;

 import me.jinjjahalgae.domain.contract.enums.ContractStatus;
 import me.jinjjahalgae.domain.contract.enums.ContractType;
 import me.jinjjahalgae.domain.participation.enums.Role;

/**
 * 계약 상세 response
 * 계약서 미리보기 등 여기저기 사용
 */

 public record ContractDetailResponse(
        Long contractId, //계약 id
        String contractUuid, //계약 uuid
        String title, //목표 제목
        String goal, //목표 상세
        String penalty, //벌칙
        String reward, //보상
        ContractType type, //계약서 디자인 타입

        LocalDateTime startDate, //시작 날짜
        LocalDateTime endDate, //종료 날짜
        ContractStatus contractStatus, //현재 상태(진행중 등)

        int proofPerWeek, //설정한 한 주 인증 수
        int totalProof, // 총 필요한 인증 수
        int currentProof, //지금까지 성공한 인증 수
        int remainingLife, //남아있는 목숨

        double achievementPercent, // 계산해서 제공
        double periodPercent,      // 계산해서 제공

        List<ParticipantInfo> participants //감독자로 참여한 유저 정보
 ) {
     public record ParticipantInfo(
         Long userId,
         String name,
         Role role,
         String signatureImageKey
     ) {}
 }