package aws.todolist.taskflow.utils;

import aws.todolist.taskflow.entity.*;
import aws.todolist.taskflow.exceptions.ProjectException.ForbiddenException;
import aws.todolist.taskflow.exceptions.errorCode.SystemErrorCode;
import aws.todolist.taskflow.messaging.kafka.message.NotificationMessage;
import aws.todolist.taskflow.messaging.kafka.message.NotificationType;
import aws.todolist.taskflow.messaging.kafka.producer.KafkaNotificationProducer;
import aws.todolist.taskflow.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationUtils {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");


    // Role để thông báo tới user trong trường hợp liên quan tới member;
    private Member memberGlobal = null;

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private KafkaNotificationProducer kafkaNotificationProducer;

    /**
     * Lấy danh sách người nhận các thông báo là bên task Service.
     * Trả về Set để loại bỏ trùng.
     */
    public Set<Account> getReceiversForTask(Task task) {
        Set<Account> receivers = new HashSet<>();

        Account accountAuthor = getAccountAuthor();

        if (task.getAccountAssign() != null && !Objects.equals(task.getAccountAssign().getId(), accountAuthor.getId())) {
            receivers.add(task.getAccountAssign());
        }

        if (!Objects.equals(task.getCreatedByAccount().getId(), accountAuthor.getId())) {
            receivers.add(task.getCreatedByAccount());
        }

        return receivers;
    }


    /**
     * Lấy danh sách các id cần gửi cho memberservice
     */
    public Set<Account> getReceiversForMemberUpdate(Member member) {
        Set<Account> receivers = new HashSet<>();

        List<Member> listMemberInProject = memberRepository.findAllByProjectId(member.getProject().getId());

        for (Member memberInList : listMemberInProject) {
            receivers.add(memberInList.getAccount());
        }

        memberGlobal = member;

        return receivers;
    }

    public Set<Account> getReceiversForMemberAdd(Member member) {
        Set<Account> receivers = new HashSet<>();

        receivers.add(member.getAccount());

        memberGlobal = member;

        return receivers;
    }

    /**
     * Lấy danh sách id cần gửi cho task comment
     */
    public Set<Account> getReceiversForTaskComment(Task task) {
        Set<Account> receivers = new HashSet<>();

        Account accountAuthor = getAccountAuthor();

        if (!Objects.equals(task.getCreatedByAccount().getId(), accountAuthor.getId())) {
            receivers.add(task.getCreatedByAccount());
        }

        for (TaskComment comment : task.getTaskComments()) {
            if (!Objects.equals(comment.getAccount().getId(), accountAuthor.getId())) {
                receivers.add(comment.getAccount());
            }
        }

        return receivers;
    }

    public Set<Account> getReceiversForProject(List<Member> members) {
        return members.stream()
                .map(Member::getAccount)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Account getAccountAuthor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof Account) {
            return (Account) principal;
        } else {
            throw new ForbiddenException(SystemErrorCode.SYS_TASKFLOW_ACCESS_DENIED,
                    "User not authenticated or invalid principal");
        }
    }

    // Hàm để gửi thông báo qua kafka cho toàn bộ loại thông báo khác nhau
    public void sendNotification(Task task,
                                 Project project,
                                 Account actor,
                                 Set<Account> accountReceiverSet,
                                 NotificationType type) {
        if (accountReceiverSet.isEmpty()) return;

        if (actor == null) {
            actor = getAccountAuthor();
        }

        for (Account accountReceiver : accountReceiverSet) {
            if (Objects.equals(actor.getId(), accountReceiver.getId())) {
                continue;
            }
            try {
                NotificationMessage message = NotificationMessage.builder()
                        .receiverId(accountReceiver.getId())
                        .actorId(actor.getId())
                        .projectId(project.getId())
                        .taskId(task != null ? task.getId() : null)
                        .type(type)
                        .build();

                switch (message.getType()) {
                    case TASK_COMPLETED: {
                        message.setTitle("Nhiệm vụ vừa hoàn thành");
                        message.setContent(String.format(
                                "Nhiệm vụ \"%s\" trong dự án \"%s\" đã được hoàn thành vào lúc \"%s\".",
                                task.getTitle(),
                                task.getSection().getProject().getName(),
                                task.getCompletedAt().format(formatter)
                        ));
                        kafkaNotificationProducer.sendTaskCompleted(message);
                        break;
                    }
                    case TASK_ASSIGNED: {
                        message.setTitle("Phân công nhiệm vụ được cập nhật");
                        message.setContent(String.format(
                                "Nhiệm vụ \"%s\" trong dự án \"%s\" đã được giao cho \"%s\".",
                                task.getTitle(),
                                task.getSection().getProject().getName(),
                                task.getAccountAssign().getDisplayName()
                        ));
                        kafkaNotificationProducer.sendTaskAssigned(message);
                        break;
                    }
                    case TASK_UPDATED: {
                        message.setTitle("Nhiệm vụ vừa được chỉnh sửa");
                        message.setContent(String.format(
                                "Nhiệm vụ \"%s\" trong dự án \"%s\" đã được chỉnh sửa.",
                                task.getTitle(),
                                task.getSection().getProject().getName()
                        ));
                        kafkaNotificationProducer.sendTaskUpdated(message);
                        break;
                    }
                    case TASK_COMMENTED: {
                        message.setTitle("Một bình luận mới được thêm vào Task");
                        message.setContent(String.format(
                                "\"%s\" vừa thêm bình luận mới vào task \"%s\" của dự án \"%s\".",
                                actor.getDisplayName(),
                                task.getTitle(),
                                task.getSection().getProject().getName()
                        ));
                        kafkaNotificationProducer.sendTaskCommented(message);
                        break;
                    }
                    case TASK_DUE_SOON: {
                        message.setTitle("Nhiệm vụ sắp đến hạn!");
                        message.setContent(String.format(
                                "Nhiệm vụ \"%s\" trong dự án \"%s\" sắp đến hạn vào lúc \"%s\".",
                                task.getTitle(),
                                task.getSection().getProject().getName(),
                                task.getDeadline().format(formatter)
                        ));
                        kafkaNotificationProducer.sendTaskDueSoon(message);
                        break;
                    }
                    case TASK_OVERDUE: {
                        message.setTitle("Nhiệm vụ bị quá hạn!");
                        message.setContent(String.format(
                                "Nhiệm vụ \"%s\" trong dự án \"%s\" đã hết hạn thực hiện vào lúc \"%s\".",
                                task.getTitle(),
                                task.getSection().getProject().getName(),
                                task.getDeadline().format(formatter)
                        ));
                        kafkaNotificationProducer.sendTaskOverdue(message);
                        break;
                    }
                    case PROJECT_DELETED: {
                        message.setTitle("Dự án đã bị xóa");
                        message.setContent(String.format(
                                "Dự án \"%s\" đã bị \"%s\" xóa khỏi hệ thống.",
                                project.getName(),
                                actor.getDisplayName()
                        ));
                        kafkaNotificationProducer.sendProjectDeleted(message);
                        break;
                    }
                    case PROJECT_MEMBER_ADDED: {
                        message.setTitle("Bạn vừa được mời vào dự án");
                        message.setContent(String.format(
                                "Bạn đã được thêm vào dự án '%s' với vai trò '%s'.",
                                project.getName(),
                                memberGlobal.getRole().name()
                        ));
                        kafkaNotificationProducer.sendProjectMemberAdded(message);
                        break;
                    }
                    case PROJECT_MEMBER_ROLE_UPDATED: {
                        message.setTitle("Vai trò của một thành viên trong dự án đã được cập nhật");
                        message.setContent(String.format(
                                "Vai trò mới của '%s' trong dự án '%s' là: '%s'",
                                memberGlobal.getAccount().getDisplayName(),
                                project.getName(),
                                memberGlobal.getRole().name()
                        ));
                        kafkaNotificationProducer.sendProjectMemberRoleUpdated(message);
                        break;
                    }
                    case REQUEST_ACCEPTED: {
                        message.setTitle("Lời mời đã được chấp nhận");
                        message.setContent(String.format("%s đã chấp nhận lời mời vào project %s",
                                actor.getDisplayName(),
                                project.getName()
                        ));
                        kafkaNotificationProducer.sendRequestAccepted(message);
                        break;
                    }
                    case REQUEST_DECLINED: {
                        message.setTitle("Lời mời đã bị từ chối");
                        message.setContent(String.format("%s đã từ chối lời mời vào project %s",
                                actor.getDisplayName(),
                                project.getName()
                        ));
                        kafkaNotificationProducer.sendRequestDeclined(message);
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Unknown notification type: " + message.getType());
                }

                System.out.printf("📤 [Kafka] Sent %s to account '%s'%n",
                        type, accountReceiver.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Gửi notification " + type + " thất bại: " + e.getMessage());
            }
        }

    }

}
