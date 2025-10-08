package aws.todolist.notification.messaging.kafka.consumer;

import aws.todolist.notification.messaging.kafka.message.RegisterEmailPayload;
import aws.todolist.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	
	@Autowired
	private EmailService emailService;
	
	@KafkaListener(topics = "${app.kafka.topic.auth.register-email}", groupId = "${spring.kafka.consumer.group-id}")
	public void listenRegisterEmail(String message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RegisterEmailPayload payload = mapper.readValue(message, RegisterEmailPayload.class);
			emailService.sendRegistrationUserConfirm(payload.getEmail(), payload.getOtp());
			System.err.println("Đã )gửi)");
		} catch (Exception e) {
			System.err.println("Failed to parse register email message: " + e.getMessage());
		}
	}
	
	@KafkaListener(topics = "${app.kafka.topic.auth.reset-password-email}", groupId = "${spring.kafka.consumer.group-id}")
	public void listenResetPasswordEmail(String message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RegisterEmailPayload payload = mapper.readValue(message, RegisterEmailPayload.class);
			emailService.sendResetPasswordUserConfirm(payload.getEmail(), payload.getOtp());
		} catch (Exception e) {
			System.err.println("Failed to parse reset password email message: " + e.getMessage());
		}
	}
	
	@KafkaListener(topics = "${app.kafka.topic.auth.update-email}", groupId = "${spring.kafka.consumer.group-id}")
	public void listenUpdateEmail(String message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			RegisterEmailPayload payload = mapper.readValue(message, RegisterEmailPayload.class);
			emailService.sendUpdateEmailOtp(payload.getEmail(), payload.getOtp());
		} catch (Exception e) {
			System.err.println("Failed to parse update email message: " + e.getMessage());
		}
	}
	
}