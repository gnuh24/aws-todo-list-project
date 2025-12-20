package aws.todolist.notification.messaging.kafka.dispatcher;


import aws.todolist.notification.dto.event.EventEnvelope;
import aws.todolist.notification.messaging.kafka.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventDispatcher {


    private final List<EventHandler> handlers;

    public void dispatch(EventEnvelope<?> event) {

        for (EventHandler handler : handlers) {
            if (handler.supports(event.getEventType())) {
                handler.handle(event);
                return;
            }
        }

        System.err.println("No handler for event " + event.getEventType());

    }
}