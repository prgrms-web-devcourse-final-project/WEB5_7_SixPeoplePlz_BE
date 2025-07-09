package me.jinjjahalgae.domain.notification.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.notification.enums.NotificationType;

/**
 * - 알림을 클릭했을 때 계약 상세에 접근하게 될 예정임.
 * 근데 해당 페이지에 접근할 때 알림을 받은 유저의 정보, 권한에 따라 접근을 막아야할 때도 있음.
 * 알림 생성 시점과 알림 클릭 시점의 차이가 있을 수 있기 때문에 미리 유저와 계약의 추가적인 정보를 넘겨주면
 * 의미가 없기 때문에 ID만 넘겨주는 것으로 결정함.
 *
 * - 프론트 측에서 페이지 이동을 위해 계약의 다른 필드에 대한 정보 없이 id만 있어도 될 것으로 판단함
 */
@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림 ID (PK)

    private Long userId; // 알림을 받을 유저 ID 정보 (FK)

    private Long contractId; // 클릭했을 때 이동할 계약 ID 정보 (FK)

    private String content; // 알림 본문

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 유형

    private boolean read = false; // 알림 조회여부 (기본값 false)

    @Builder
    public Notification(Long userId, Long contractId, String content, NotificationType type) {
        this.userId = userId;
        this.contractId = contractId;
        this.content = content;
        this.type = type;
    }

    /// 해당 알림객체를 읽음 상태로 변경
    public void markRead() {
        this.read = true;
    }
}
