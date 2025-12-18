package aws.todolist.auth.messaging.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${app.kafka.topic.auth.register-email}")
	private String registerEmailTopic;
	
	@Value("${app.kafka.topic.auth.reset-password-email}")
	private String resetPasswordEmailTopic;
	
	@Value("${app.kafka.topic.auth.update-email}")
	private String updateEmailTopic;
	
	@Value("${app.kafka.topic.auth.delete-account}")
	private String deleteAccountTopic;   // 👈 NEW
	
	public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	/* =====================================================
	 *                   SEND EMAIL
	 * ===================================================== */
	
	public void sendRegisterEmail(String email, String otp) {
		kafkaTemplate.send(registerEmailTopic, buildMessage(email, otp));
	}
	
	public void sendResetPasswordEmail(String email, String otp) {
		kafkaTemplate.send(resetPasswordEmailTopic, buildMessage(email, otp));
	}
	
	public void sendUpdateEmail(String email, String otp) {
		kafkaTemplate.send(updateEmailTopic, buildMessage(email, otp));
	}
	
	public void sendDeleteAccount(String email, String otp) {
		kafkaTemplate.send(deleteAccountTopic, buildMessage(email, otp));
	}
	
	/* =====================================================
	 *                   BUILDER
	 * ===================================================== */
	
	private String buildMessage(String email, String otp) {
		// JSON đơn giản – consumer email service xử lý
		return String.format(
			"{\"email\":\"%s\",\"otp\":\"%s\"}",
			email,
			otp
		);
	}
}
