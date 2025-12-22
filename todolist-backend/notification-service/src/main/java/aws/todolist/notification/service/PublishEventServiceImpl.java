package aws.todolist.notification.service;

import aws.todolist.notification.dto.eventNotification.NotificationEventPayload;
import aws.todolist.notification.dto.notification.NotificationResponse;
import aws.todolist.notification.entity.Account;
import aws.todolist.notification.entity.Notification;
import aws.todolist.notification.mapper.NotificationMapper;
import aws.todolist.notification.messaging.kafka.producer.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishEventServiceImpl implements PublishEventService{

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private NotificationMapper notificationEventMapper;


    @Override
    public void publish(Notification notification) {

        Account receiver = notification.getReceiver();

        NotificationResponse response = notificationEventMapper.toResponse(notification);

        // map response + email -> payload
        NotificationEventPayload payload = notificationEventMapper.toEventPayload(receiver.getEmail(), response);

        // publish kafka
        kafkaProducerService.sendNotificationEvent(receiver.getId(), payload);
    }
}
