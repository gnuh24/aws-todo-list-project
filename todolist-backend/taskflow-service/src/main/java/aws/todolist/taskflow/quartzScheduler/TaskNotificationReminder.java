package aws.todolist.taskflow.quartzScheduler;

import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.messaging.kafka.producer.KafkaNotificationProducer;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.service.TaskServiceImpl;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskNotificationReminder implements Job {


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");

    @Autowired
    private KafkaNotificationProducer kafkaNotificationProducer;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String taskId = context.getMergedJobDataMap().getString("taskId");
        String actor = context.getMergedJobDataMap().getString("actor");

        JobDataMap dataMap = context.getTrigger().getJobDataMap(); // 👈 lấy từ trigger
        String typeStr = dataMap.getString("type");
        NotificationType type = NotificationType.valueOf(typeStr);

        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId);

        if (task == null) {
            System.err.printf("Task %s không tồn tại", taskId);
            return;
        }

        // Thông báo kafka

        List<Account> listAccountReceiver = new ArrayList<>();
        listAccountReceiver.add(task.getAccountAssign());
        listAccountReceiver.add(task.getCreatedByAccount());
        String title;
        String content;

        if (type == NotificationType.TASK_DUE_SOON) {
            title = "Nhiệm vụ sắp đến hạn!";
            content = String.format(
                    "Nhiệm vụ \"%s\" trong dự án \"%s\" sắp đến hạn vào lúc \"%s\".",
                    task.getTitle(),
                    task.getSection().getProject().getName(),
                    task.getDeadline().format(formatter)
            );
        } else {
            title = "Nhiệm vụ bị quá hạn!";
            content = String.format(
                    "Nhiệm vụ \"%s\" trong dự án \"%s\" đã hết hạn thực hiện vào lúc \"%s\".",
                    task.getTitle(),
                    task.getSection().getProject().getName(),
                    task.getDeadline().format(formatter)
            );
        }

        for (Account account : listAccountReceiver) {
            sendNotification(task, account, actor, type, title, content);
        }
    }

    // Hàm để gửi thông báo qua kafka cho toàn bộ loại thông báo khác nhau
    private void sendNotification(Task task,
                                  Account accountReceiver,
                                  String actorId,
                                  NotificationType type,
                                  String title,
                                  String content) {
        if (accountReceiver == null) return;

        try {
            NotificationMessage message = NotificationMessage.builder()
                    .receiverId(accountReceiver.getId())
                    .actorId(actorId)
                    .projectId(task.getSection().getProject().getId())
                    .taskId(task.getId())
                    .type(type)
                    .title(title)
                    .content(content)
                    .build();

            switch (message.getType()) {
                case TASK_DUE_SOON:
                    kafkaNotificationProducer.sendTaskDueSoon(message);
                    break;
                case TASK_OVERDUE:
                    kafkaNotificationProducer.sendTaskOverdue(message);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown notification type: " + message.getType());
            }

            System.out.printf("📤 [Kafka] Sent %s for task '%s' to account '%s'%n",
                    type, task.getTitle(), accountReceiver.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Gửi notification " + type + " thất bại: " + e.getMessage());
        }
    }


}
