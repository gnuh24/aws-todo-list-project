package aws.todolist.taskflow.quartzScheduler;


import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TaskSchedulerService {

    @Autowired
    private Scheduler scheduler;

    /**
     * Tạo trigger cho task với nhiều mốc nhắc
     *
     * @param taskId   ID task
     * @param deadline Thời điểm deadline
     */

    public void scheduleTaskWithReminders(String taskId, LocalDateTime deadline) throws SchedulerException {

        // JobKey để quản lý job
        JobKey jobKey = new JobKey("taskJob_" + taskId);

        // Nếu job chưa tồn tại, tạo JobDetail
        if (!scheduler.checkExists(jobKey)) {
            JobDetail jobDetail = JobBuilder.newJob(TaskNotificationReminder.class)
                    .withIdentity(jobKey)
                    .usingJobData("taskId", taskId)
                    .usingJobData("actor", "11111111-1111-1111-1111-111111111111")
                    .usingJobData("deadline", deadline.toString())
                    .storeDurably()
                    .build();

            scheduler.addJob(jobDetail, true);
        } else //Nếu jobkey tồn tại thì lấy deadline có lưu trong jobkey ra kiểm tra nếu khác thì làm lại trigger
        {
            // Lấy ra job theo jobkey
            JobDetail existingJob = scheduler.getJobDetail(jobKey);
            JobDataMap dataMap = existingJob.getJobDataMap();
            String oldDeadlineStr = dataMap.getString("deadline");
            LocalDateTime oldDeadline = LocalDateTime.parse(oldDeadlineStr);

            // 3️⃣ Nếu deadline không thay đổi → bỏ qua reschedule
            if (oldDeadline.truncatedTo(ChronoUnit.SECONDS).equals(deadline.truncatedTo(ChronoUnit.SECONDS))) {
                System.out.println("Deadline không thay đổi, bỏ qua reschedule cho task " + taskId);
                return;
            } else { // Nếu có khác thì cập nhật lại jobData cho lần sau sử dụng tiếp

                existingJob.getJobDataMap().put("deadline", deadline.toString());

                System.out.println("Updated JobDataMap with new deadline for task " + taskId);
            }
        }


        // Mốc nhắc nhở trước deadline
        // Tạo các mốc nhắc
        LocalDateTime[] reminderTimes = new LocalDateTime[]{
                deadline.minusDays(3),   // 3 ngày trước
                deadline.minusDays(1),   // 1 ngày trước
        };

        for (int i = 0; i < reminderTimes.length; i++) {
            LocalDateTime reminder = reminderTimes[i];
            Date triggerTime = Date.from(reminder.atZone(ZoneId.systemDefault()).toInstant());

            String triggerKeyStr = "taskTrigger_" + taskId + "_" + i;
            TriggerKey triggerKey = new TriggerKey(triggerKeyStr);

            // Nếu trigger đã tồn tại → xoá trước khi tạo mới
            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobKey)
                    .withIdentity(triggerKey)
                    .startAt(triggerTime)
                    .usingJobData("type", String.valueOf(NotificationType.TASK_DUE_SOON))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow()) // nếu bỏ lỡ, chạy ngay
                    .build();

            scheduler.scheduleJob(trigger);
            System.out.println("✅ Trigger đã tạo lịch nhắc deadline cho task " + taskId + " lúc " + reminder);
        }


        // Mốc nhắc nhở sau deadline
        LocalDateTime reminderOverDue = deadline.plusMinutes(5);
        Date triggerTime = Date.from(reminderOverDue.atZone(ZoneId.systemDefault()).toInstant());

        String triggerKeyStr = "taskTrigger_" + taskId;
        TriggerKey triggerKey = new TriggerKey(triggerKeyStr);

        // Nếu trigger đã tồn tại → xoá trước khi tạo mới
        if (scheduler.checkExists(triggerKey)) {
            scheduler.unscheduleJob(triggerKey);
        }

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(triggerKey)
                .startAt(triggerTime)
                .usingJobData("type", String.valueOf(NotificationType.TASK_OVERDUE))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow()) // nếu bỏ lỡ, chạy ngay
                .build();

        scheduler.scheduleJob(trigger);
        System.out.println("✅ Trigger đã tạo lịch thông báo trễ deadline cho task " + taskId + " lúc " + reminderOverDue);

    }


    public void deleteTaskSchedule(String taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("taskJob_" + taskId);
        scheduler.deleteJob(jobKey);
        System.out.println("🗑️ Đã xoá lịch cho task " + taskId);
    }

    public boolean isJobExists(String taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("taskJob_" + taskId);
        return scheduler.checkExists(jobKey);
    }


}
