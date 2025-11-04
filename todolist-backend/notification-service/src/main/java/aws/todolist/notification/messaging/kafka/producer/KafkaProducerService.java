package aws.todolist.notification.messaging.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

//    private final KafkaTemplate<String, String> kafkaTemplate;

//    @Value("${app.kafka.topic.auth.register-email}")
//    private String registerEmailTopic;
//
//    @Value("${app.kafka.topic.auth.reset-password-email}")
//    private String resetPasswordEmailTopic;
//
//    @Value("${app.kafka.topic.auth.update-email}")
//    private String updateEmailTopic;
//
//    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendRegisterEmail(String email, String otp) {
//        String message = buildMessage(email, otp);
//        kafkaTemplate.send(registerEmailTopic, message);
//    }
//
//    public void sendResetPasswordEmail(String email, String otp) {
//        String message = buildMessage(email, otp);
//        kafkaTemplate.send(resetPasswordEmailTopic, message);
//    }
//
//    public void sendUpdateEmail(String email, String otp) {
//        String message = buildMessage(email, otp);
//        kafkaTemplate.send(updateEmailTopic, message);
//    }
//
//    private String buildMessage(String email, String otp) {
//        // Dạng JSON đơn giản
//        return String.format("{\"email\":\"%s\",\"otp\":\"%s\"}", email, otp);
//    }
}
