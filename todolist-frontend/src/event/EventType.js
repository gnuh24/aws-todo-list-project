/**
 * EventType
 * =========
 * ⚠️ MUST match backend enum aws.todolist.taskflow.enums.EventType
 * Không đổi tên – không thêm logic
 */

export const EVENT = {
    // =========================
    // 🧩 PROJECT EVENTS
    // =========================
    PROJECT_CREATED: "PROJECT_CREATED",
    PROJECT_UPDATED: "PROJECT_UPDATED",
    PROJECT_DELETED: "PROJECT_DELETED",
    PROJECT_ARCHIVED: "PROJECT_ARCHIVED",

    // =========================
    // 👥 PROJECT MEMBER EVENTS
    // =========================
    PROJECT_MEMBER_ADDED: "PROJECT_MEMBER_ADDED",
    PROJECT_MEMBER_REMOVED: "PROJECT_MEMBER_REMOVED",
    PROJECT_MEMBER_ROLE_UPDATED: "PROJECT_MEMBER_ROLE_UPDATED",
    PROJECT_MEMBER_ACCEPTED: "PROJECT_MEMBER_ACCEPTED",
    PROJECT_MEMBER_DECLINED: "PROJECT_MEMBER_DECLINED",

    // =========================
    // 🧩 SECTION EVENTS
    // =========================
    SECTION_CREATED: "SECTION_CREATED",
    SECTION_NAME_UPDATED: "SECTION_NAME_UPDATED",
    SECTION_DELETED: "SECTION_DELETED",
    SECTION_MOVED: "SECTION_MOVED",

    // =========================
    // 🧩 TASK EVENTS
    // =========================

    // ===== CRUD =====
    TASK_CREATED: "TASK_CREATED",
    TASK_UPDATED: "TASK_UPDATED",
    TASK_DELETED: "TASK_DELETED",
    TASK_RESTORED: "TASK_RESTORED",
    TASK_ARCHIVED: "TASK_ARCHIVED",

    // ===== STATE / PROPERTY =====
    TASK_STATUS_UPDATED: "TASK_STATUS_UPDATED",
    TASK_PRIORITY_UPDATED: "TASK_PRIORITY_UPDATED",
    TASK_SECTION_UPDATED: "TASK_SECTION_UPDATED",

    // ===== RELATION =====
    TASK_RELATIONSHIP_UPDATED: "TASK_RELATIONSHIP_UPDATED",

    // ===== ASSIGNEE =====
    TASK_ASSIGNED: "TASK_ASSIGNED",
    TASK_UNASSIGNED: "TASK_UNASSIGNED",

    // ===== Quartz =====
    TASK_DUE_SOON: "TASK_DUE_SOON",
    TASK_OVERDUE: "TASK_OVERDUE",

    // =========================
    // 💬 COMMENT EVENTS
    // =========================
    COMMENT_CREATED: "COMMENT_CREATED",
    COMMENT_UPDATED: "COMMENT_UPDATED",
    COMMENT_DELETED: "COMMENT_DELETED",
};
