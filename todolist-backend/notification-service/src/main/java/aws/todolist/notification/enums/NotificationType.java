package aws.todolist.notification.enums;

public enum NotificationType {

    // ===== PROJECT =====
    PROJECT_MEMBER_ADDED,
    PROJECT_MEMBER_ROLE_UPDATED,
    PROJECT_DELETED,

    // ===== REQUEST =====
    REQUEST_ACCEPTED,
    REQUEST_DECLINED,

    // ===== TASK =====
    TASK_ASSIGNED,
    TASK_COMPLETED,
    TASK_REOPENED,
    TASK_UPDATED,
    TASK_DUE_SOON,
    TASK_OVERDUE,

    // ===== COMMENT =====
    TASK_COMMENTED
}
