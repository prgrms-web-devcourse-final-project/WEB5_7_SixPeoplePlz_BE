package me.jinjjahalgae.domain.common;

/**
 * 단일 비즈니스 유스케이스를 정의하는 공통 인터페이스
 * 프로젝트 내 코드 일관성 유지용
 * 프로젝트 내의 모든 유스케이스 명세는 이 인터페이스를 상속해서 구현
 * 그렇게 되면 명세를 먼저 작성하고 구현에 들어가게 됨
 *
 * @param <REQ> 유스케이스 실행에 필요한 입력 데이터 (Request DTO)
 * @param <RES> 유스케이스 실행 후의 출력 결과 (Response DTO)
 */
public interface UseCase<REQ, RES> {
    RES execute(REQ req);
}
