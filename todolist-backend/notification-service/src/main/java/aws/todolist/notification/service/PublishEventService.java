package aws.todolist.notification.service;

import aws.todolist.notification.entity.Notification;

public interface PublishEventService {

    void publish(Notification notification);
}