package aws.todolist.taskflow.quartzScheduler;

import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.messaging.kafka.producer.KafkaNotificationProducer;
import aws.todolist.taskflow.repository.AccountRepository;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.service.TaskServiceImpl;
import aws.todolist.taskflow.utils.NotificationUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TaskNotificationReminder implements Job {


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");

    private final String IdAccountSystem = "11111111-1111-1111-1111-111111111111";

    @Autowired
    private KafkaNotificationProducer kafkaNotificationProducer;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private NotificationUtils notificationUtils;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String taskId = context.getMergedJobDataMap().getString("taskId");

        JobDataMap dataMap = context.getTrigger().getJobDataMap(); // 👈 lấy từ trigger
        String typeStr = dataMap.getString("type");
        NotificationType type = NotificationType.valueOf(typeStr);

        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId);

        Optional<Account> account = accountRepository.findByIdAndIsDeletedFalse(IdAccountSystem);

        if (task == null) {
            System.err.printf("Task %s không tồn tại", taskId);
            return;
        }

        if (account.isEmpty()) {
            System.err.printf("Account %s không tồn tại", IdAccountSystem);
            return;
        }

        // Thông báo kafka

        notificationUtils.sendNotification(task, task.getSection().getProject(), account.get(), notificationUtils.getReceiversForTask(task), type);

    }

}
