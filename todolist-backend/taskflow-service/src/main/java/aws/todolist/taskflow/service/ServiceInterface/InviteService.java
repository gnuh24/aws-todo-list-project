package aws.todolist.taskflow.service.ServiceInterface;

public interface InviteService {
    String createInviteLink(String projectId);

    void verifyInvite(String token);
}
