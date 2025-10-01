package com.aws.todolist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task")
@Builder
public class Task {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2") // sinh UUID
    // TODO: Lưu ý @Column(length = 36) // UUID chuẩn dài 36 ký tự
    @Column(name = "id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    @Builder.Default
    private String description = null;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private Boolean isArchived = false;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private Status status = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    @Builder.Default
    private Priority priority = null;

    @Column(name = "deadline")
    @Builder.Default
    private LocalDateTime deadline = null;

    @Column(name = "start_time")
    @Builder.Default
    private LocalDateTime startTime = null;

    @Column(name = "completedAt")
    @Builder.Default
    private LocalDateTime completedAt = null;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @Builder.Default
    private LocalDateTime deletedAt = null;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "task_father_id")
    @Builder.Default
    private Task taskFather = null; // quan hệ cha-con có thể tự join bằng code nếu cần

    @OneToMany(mappedBy = "taskFather", cascade = CascadeType.ALL)
    @Builder.Default
    private ArrayList<Task> taskChild = null;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @Builder.Default
    private Section section = null;

    // TODO: Tách riêng
    // Enum cho status
    public enum Status {
        PENDING, READY, IN_PROGRESS, COMPLETED
    }

    // Enum cho priority
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}
