package aws.todolist.taskflow.quartzScheduler;

import aws.todolist.taskflow.entity.Task;
import aws.todolist.taskflow.messaging.kafka.message.EventType;
import aws.todolist.taskflow.repository.TaskRepository;
import aws.todolist.taskflow.service.ServiceEventKafka.TaskEventService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class TaskEventReminder implements Job {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskEventService taskEventService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String taskId = context.getMergedJobDataMap().getString("taskId");

        JobDataMap dataMap = context.getTrigger().getJobDataMap(); // 👈 lấy từ trigger
        String typeStr = dataMap.getString("type");
        EventType type = EventType.valueOf(typeStr);

        Task task = taskRepository.findByIdWithLabels(taskId);

        if (task == null) {
            System.err.printf("Task %s không tồn tại", taskId);
            return;
        }


        // gửi event
        switch (type) {
            case TASK_DUE_SOON: {
                taskEventService.publishDueSoon(task);
            }

            case TASK_OVERDUE: {
                taskEventService.publishOverdue(task);
            }

        }

    }

}
