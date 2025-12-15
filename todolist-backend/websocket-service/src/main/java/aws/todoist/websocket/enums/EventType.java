package aws.todoist.websocket.enums;

public enum EventType {

    // =========================
    // 🧩 PROJECT EVENTS
    // =========================
    PROJECT_CREATED,
    PROJECT_UPDATED,
    PROJECT_DELETED,
    PROJECT_ARCHIVED,
    PROJECT_RESTORED,

    // =========================
    // 👥 PROJECT MEMBER EVENTS
    // =========================
    PROJECT_MEMBER_ADDED,
    PROJECT_MEMBER_REMOVED,
    PROJECT_MEMBER_ROLE_UPDATED,

    // =========================
    // 🧩 SECTION EVENTS
    // =========================
    SECTION_CREATED,
    SECTION_UPDATED,
    SECTION_DELETED,
    SECTION_MOVED,

    // =========================
    // 🧩 TASK EVENTS
    // =========================
    TASK_CREATED,
    TASK_UPDATED,
    TASK_DELETED,

    TASK_COMPLETED,
    TASK_REOPENED,

    TASK_ASSIGNED,
    TASK_UNASSIGNED,

    TASK_MOVED_PROJECT,
    TASK_MOVED_SECTION,

    TASK_DUE_DATE_UPDATED,
    TASK_PRIORITY_UPDATED,

    // =========================
    // 💬 COMMENT EVENTS
    // =========================
    COMMENT_CREATED,
    COMMENT_UPDATED,
    COMMENT_DELETED
}
