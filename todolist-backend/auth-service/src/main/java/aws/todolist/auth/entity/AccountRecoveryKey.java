package aws.todolist.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_recovery_key")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRecoveryKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    /**
     * Hash của recovery key (bcrypt / sha256)
     * Không bao giờ lưu plain text
     */
    @Column(name = "key_hash", nullable = false, unique = true, length = 255)
    private String keyHash;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* ===============================
     *          LIFECYCLE
     * =============================== */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        isUsed = false;
    }
}
