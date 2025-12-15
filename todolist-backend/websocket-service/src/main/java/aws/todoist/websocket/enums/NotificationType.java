package aws.todoist.websocket.enums;

// ==== ENUM LOẠI THÔNG BÁO ====
// Có thể đặt trong file riêng hoặc giữ lại như sau:
public enum NotificationType {
    PROJECT_MEMBER_ADDED,         // Khi được thêm vào project
    PROJECT_MEMBER_ROLE_UPDATED,  // Khi vai trò trong project thay đổi
    TASK_ASSIGNED,                // Khi được giao task
    TASK_COMMENTED,               // Khi có người comment vào task liên quan
    TASK_UPDATED,                 // Khi task được cập nhật
    TASK_COMPLETED,               // Khi task hoàn thành
    TASK_REOPENED,                // Khi task bị mở lại
    TASK_DUE_SOON,                // Khi task sắp đến hạn
    TASK_OVERDUE,                 // Khi task bị trễ hạn
    PROJECT_DELETED,               // Khi project bị xóa
    REQUEST_ACCEPTED,               // Khi thành viên chấp nhận lời mời
    REQUEST_DECLINED                // Khi thành viên từ chối lời mời
}
