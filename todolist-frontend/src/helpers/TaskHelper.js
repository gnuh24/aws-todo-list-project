// src/helpers/TaskHelper.js
class TaskHelper {
    static statusColors = {
        PENDING: "gray",
        READY: "blue",
        IN_PROGRESS: "orange",
        COMPLETED: "green",
        CANCELLED: "red",
    };

    static normalizeTask(task) {
        return {
            ...task,
            taskChild: task.taskChild || [],
            comments: task.comments || [],
            labels: task.labels || [],
        };
    }

    // Có thể thêm nhiều helper liên quan task ở đây
}

export default TaskHelper;
