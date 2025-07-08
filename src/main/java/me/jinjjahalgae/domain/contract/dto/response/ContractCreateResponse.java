package me.jinjjahalgae.domain.contract.dto.response;

/**
 * 인증 생성 response
 * 생성 완료 응답에는 많은 정보가 필요하지 않지만 id와 uuid를 볼 수 있게 하기 위해서 구현
 */
public record ContractCreateResponse(
        Long contractId,
        String contractUuid
) {}