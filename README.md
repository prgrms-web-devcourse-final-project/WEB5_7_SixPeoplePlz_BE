# 진짜할게

팀원: [김광현](https://github.com/kwang2134), [탁서윤](https://github.com/peng255), [이상진](https://github.com/silkair), [황치연](https://github.com/inswal843), [김수혁](https://github.com/Soohyeok447)

[진짜할게 플레이스토어 URL](https://play.google.com/store/apps/details?id=com.jinjahalgae.app)

## 프로젝트 소개

**진짜할게**는 사용자의 목표 달성을 위해 지인과 '계약'을 맺고, 인증 사진을 통해 진행 상황을 관리하는 모바일 앱입니다. <br>
사용자가 스스로 목표를 설정하고 지인을 감독자로 초대하여, 객관적인 검증을 통해 목표 달성률을 높이는 것을 목표로 합니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/4ae9e2cc-bbb5-47c1-a642-3ae8b6edab65" alt="1" width="21%">
  &nbsp;
  <img src="https://github.com/user-attachments/assets/eefde38e-6ed3-4177-9fa6-e49251fe5ada" alt="2" width="21%">
  &nbsp;
  <img src="https://github.com/user-attachments/assets/c4782609-beb5-432c-941d-b3a440d76256" alt="3" width="21%">
  &nbsp;
  <img src="https://github.com/user-attachments/assets/e94ac032-57c1-4c6c-afde-9ee29df6b628" alt="4" width="21%">
</p>

<br>

## 사용한 기술

### 개발

-   **Java 21**
-   **Spring Boot 3.5.3**
-   **Spring Security**
-   **JPA/Hibernate**
-   **Firebase FCM**

### DB

-   **MySQL**: 계약과 인증의 복잡한 관계를 모델링하고, 데이터 무결성을 보장하기 위해 RDBMS 선택
-   **Redis**: 초대 링크 임시 저장 및 JWT 토큰 관리를 통한 성능 최적화

### 인프라

-   **AWS EC2**: Docker 컨테이너 기반 애플리케이션 배포 환경
-   **AWS S3 & CloudFront**: 인증 이미지 저장 및 CDN을 통한 빠른 콘텐츠 전송
-   **Nginx**: 리버스 프록시

### CI/CD

-   **GitHub Actions**: 코드 빌드, 테스트, Docker 이미지 빌드 및 배포 파이프라인 자동화
-   **Docker**: 컨테이너화를 통한 일관된 배포 환경 구성
-   **Prometheus & Grafana**: 애플리케이션 모니터링 및 메트릭 수집

## ERD
<img width="1160" height="702" alt="진짜할게" src="https://github.com/user-attachments/assets/34e36ff7-7a23-41ed-9fce-705b226b93f7" />

## 시스템 아키텍쳐
<img width="1572" height="760" alt="JJHG" src="https://github.com/user-attachments/assets/e2ec0752-52c3-4b07-8e6a-9887f41378ec" />

## WAS 아키텍쳐
<img width="511" height="721" alt="진짜할게 WAS" src="https://github.com/user-attachments/assets/b0ccbfd4-df14-419b-8d1d-9c0afea5aa4d" />

## 핵심 기능

### 계약(Contract) 관리 시스템

-   **계약 생성 및 관리**: 목표 달성을 위한 계약 생성, 수정, 삭제 기능
-   **참여자 관리**: 계약자(CONTRACTOR)와 감독자(SUPERVISOR) 역할 기반 참여 시스템
-   **초대 링크**: Redis 기반 임시 초대 링크 생성 및 검증
-   **계약 상태 관리**: PENDING → ACTIVE → COMPLETED/FAILED 라이프사이클

### 인증(Proof) 시스템

-   **인증 제출**: 계약 이행을 증명하는 사진 및 텍스트 인증 제출
-   **인증 검토**: 감독자의 인증 승인/거절 및 피드백 기능
-   **재인증**: 거절된 인증에 대한 재제출 워크플로
-   **인증 상태 추적**: PENDING → APPROVED/REJECTED 상태 관리

### 피드백(Feedback) 시스템

-   **인증 피드백**: 감독자가 제출된 인증에 대해 코멘트와 함께 승인/거절 처리
-   **피드백 상태 관리**: 피드백 생성, 수정, 삭제 및 상태 추적
-   **연관관계 관리**: Proof 도메인과의 연관관계를 통한 체계적인 피드백 관리

### 인증/인가 시스템

-   **소셜 로그인**: 전략 패턴을 활용한 카카오/네이버 OAuth2 로그인
-   **JWT 토큰 관리**: Access Token/Refresh Token 기반 인증
-   **보안 설정**: Spring Security를 활용한 API 보안 및 권한 관리

### 알림(Notification) 시스템

-   **Firebase FCM**: 계약 상태, 인증 요청 등 실시간 푸시 알림
-   **이벤트 기반 알림**: `@EventListener`를 활용한 비동기 알림 처리
-   **알림 히스토리**: 알림 목록 조회, 읽음 처리, 삭제 기능

### 파일 관리

-   **AWS S3**: Presigned URL을 통한 안전한 이미지 업로드

### 스케줄링 시스템

-   **자동 상태 전환**: Spring Scheduler를 활용한 계약/인증 상태 자동 업데이트
-   **배치 처리**: 만료된 데이터 정리 및 벌크 연산 최적화

## 아키텍처 특징

### Clean Architecture + 도메인기반

-   **도메인 기반 모듈 설계**: 각 도메인별로 독립적인 모듈 구성하여 유지보수성 확보
-   **Use Case 패턴**: 모든 비즈니스 로직을 Use Case로 분리하여 단일 책임 원칙 준수
-   **계층 분리**: Presentation, Domain, Infrastructure 계층의 명확한 관심사 분리

### 이벤트 기반 비동기 처리

-   **Spring Events**: `@EventListener`를 활용한 도메인 간 느슨한 결합
-   **비동기 알림**: `@Async`를 통한 성능 최적화된 푸시 알림 처리
-   **상태 관리**: 이벤트 기반 계약 및 인증 상태 자동 변경

### 데이터베이스 설계

-   **MySQL**: 복잡한 계약 관계 모델링과 데이터 무결성 보장
-   **Redis**: 감독자 초대 링크 임시 저장 및 동시성 제어를 통한 성능 최적화
    -   **초대 링크 관리**: UUID 기반 8자리 초대 코드와 비밀번호를 24시간 TTL로 저장
    -   **동시성 제어**: 감독자 참여 시 Redis를 활용한 원자적 연산으로 중복 참여 방지
-   **JPA**: Hibernate batch fetching으로 N+1 문제 해결

### 보안 및 인증 체계

-   **JWT 기반**: Access/Refresh Token을 통한 stateless 인증
-   **OAuth2.0**: 전략 패턴을 활용한 확장 가능한 소셜 로그인
-   **Spring Security**: 권한 기반 API 보안 및 CORS 설정

### 스케줄링 시스템

-   **Spring Scheduler**: 5분 단위 인증 만료 처리, 일일 계약 상태 전환
-   **배치 처리**: 벌크 연산을 통한 대량 데이터 효율적 처리
-   **재시도 로직**: 실패한 작업에 대한 안정적인 복구 메커니즘

### UTC 기반 시간 관리 체계

-   **표준 시간 통합**: 모든 시간 데이터를 UTC 기준으로 통합 관리하여 일관된 시간데이터 제공
-   **Instant 타입**: JPA BaseEntity에서 createdAt/updatedAt을 Instant로 관리하여 타임존 무관한 일관성 확보
-   **Jackson 설정**: ObjectMapper를 UTC 타임존으로 설정하여 JSON 직렬화/역직렬화 시 시간 일관성 보장
-   **데이터베이스 설정**: MySQL serverTimezone=UTC 및 Hibernate time_zone=UTC로 DB 레벨 시간 통일
-   **유틸리티 클래스**: UtcDateTimeUtil을 통한 UTC 기준 시간 처리 로직 표준화

## 프로젝트 구조

```
src/main/java/me/jinjjahalgae/
├── domain/              # 도메인별 비즈니스 로직
│   ├── auth/           # 인증/인가 시스템
│   ├── contract/       # 계약 관리
│   ├── proof/          # 인증 시스템
│   ├── participation/  # 참여 관리
│   ├── notification/   # 알림 시스템
│   ├── invite/         # 초대 시스템
│   ├── user/           # 사용자 관리
│   ├── feedback/       # 피드백 관리
│   └── file/           # 파일 관리
├── global/             # 공통 설정 및 유틸리티
│   ├── config/         # 설정 클래스
│   ├── security/       # 보안 설정
│   ├── exception/      # 예외 처리
│   └── common/         # 공통 응답 형식
└── presentation/       # API 컨트롤러
    └── api/            # REST API
```

각 도메인은 다음 구조를 따릅니다:

```
domain/{domain-name}/
├── entity/          # JPA 엔티티
├── enums/           # 도메인 열거형
├── repository/      # 데이터 접근 계층
├── mapper/          # Entity-DTO 변환
└── usecase/         # 비즈니스 로직
    ├── create/      # 생성 작업
    ├── get/         # 조회 작업
    ├── update/      # 수정 작업
    ├── delete/      # 삭제 작업
    └── common/      # 공통 DTO
```

## 배포 환경

**프로덕션 배포**

-   AWS EC2 기반 Docker 컨테이너 배포
-   GitHub Actions를 통한 CI/CD 파이프라인
-   Prometheus + Grafana 모니터링

**환경별 프로필**

-   `local`: 개발 환경 (MySQL localhost:3308)
-   `dev`: 개발 서버 환경
-   `prod`: 운영 환경
