package aws.todolist.auth.entity;

import aws.todolist.auth.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "notification_setting",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_setting_account_type",
                        columnNames = {"account_id", "notification_type"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting{

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_setting_account")
    )
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(name = "enable_web", nullable = false)
    private boolean enableWeb;

    @Column(name = "enable_email", nullable = false)
    private boolean enableEmail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* =========================
       Lifecycle
       ========================= */

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }


    public static NotificationSetting defaultFor(
            Account account,
            NotificationType type
    ) {
        boolean enableEmail =
                type == NotificationType.TASK_ASSIGNED
                        || type == NotificationType.TASK_COMMENTED
                        || type == NotificationType.REQUEST_ACCEPTED
                        || type == NotificationType.REQUEST_DECLINED;

        return NotificationSetting.builder()
                .account(account)
                .notificationType(type)
                .enableWeb(true)
                .enableEmail(enableEmail)
                .build();
    }
}
