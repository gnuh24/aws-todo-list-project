package aws.todolist.taskflow.messaging.kafka.message;

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
    RESPONSE_INVITATION                // Khi phản hồi lời mời
}