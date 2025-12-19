package aws.todoist.websocket.enums.eventDto;

public enum EventType {

    // =========================
    // 🧩 PROJECT EVENTS
    // =========================
    PROJECT_CREATED,
    PROJECT_UPDATED,
    PROJECT_DELETED,
    PROJECT_ARCHIVED,

    // =========================
    // 👥 PROJECT MEMBER EVENTS
    // =========================
    PROJECT_MEMBER_ADDED,
    PROJECT_MEMBER_REMOVED,
    PROJECT_MEMBER_ROLE_UPDATED,
    PROJECT_MEMBER_ACCEPTED,
    PROJECT_MEMBER_DECLINED,

    // =========================
    // 🧩 SECTION EVENTS
    // =========================
    SECTION_CREATED,
    SECTION_NAME_UPDATED,
    SECTION_DELETED,
    SECTION_MOVED,

    // =========================
    // 🧩 TASK EVENTS
    // =========================
    // ===== CRUD =====
    TASK_CREATED,
    TASK_UPDATED,
    TASK_DELETED,
    TASK_RESTORED,
    TASK_ARCHIVED,

    // ===== STATE / PROPERTY =====
    TASK_STATUS_UPDATED,
    TASK_PRIORITY_UPDATED,
    TASK_SECTION_UPDATED,

    // ===== RELATION =====
    TASK_RELATIONSHIP_UPDATED,

    // ===== ASSIGNEE =====
    TASK_ASSIGNED,
    TASK_UNASSIGNED,

    // ==== Quartz =====
    TASK_DUE_SOON,
    TASK_OVERDUE,

    // =========================
    // 💬 COMMENT EVENTS
    // =========================
    COMMENT_CREATED,
    COMMENT_UPDATED,
    COMMENT_DELETED
}
