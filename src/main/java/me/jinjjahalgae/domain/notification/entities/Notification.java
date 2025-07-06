package me.jinjjahalgae.domain.notification.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jinjjahalgae.domain.notification.enums.NotificationType;

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

    private boolean readStatus = false; // 알림 조회여부 (기본값 false)

    @Builder
    public Notification(Long userId, Long contractId, String content, NotificationType type) {
        this.userId = userId;
        this.contractId = contractId;
        this.content = content;
        this.type = type;
    }

    /// 해당 알림객체를 읽음 상태로 변경
    public void read() {
        this.readStatus = true;
    }
}
