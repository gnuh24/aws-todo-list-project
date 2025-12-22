package aws.todolist.auth.messaging.kafka.producer;

import aws.todolist.auth.otp.OtpPurpose;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class KafkaProducerService {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	private final Map<OtpPurpose, String> topicMap = new EnumMap<>(OtpPurpose.class);
	
	public KafkaProducerService(
		KafkaTemplate<String, String> kafkaTemplate,
		@Value("${app.kafka.topic.auth.register-email}") String registerEmailTopic,
		@Value("${app.kafka.topic.auth.reset-password-email}") String resetPasswordEmailTopic,
		@Value("${app.kafka.topic.auth.update-email}") String updateEmailTopic,
		@Value("${app.kafka.topic.auth.delete-account}") String deleteAccountTopic
	) {
		this.kafkaTemplate = kafkaTemplate;
		
		topicMap.put(OtpPurpose.VERIFY_ACCOUNT, registerEmailTopic);
		topicMap.put(OtpPurpose.FORGOT_PASSWORD, resetPasswordEmailTopic);
		topicMap.put(OtpPurpose.CHANGE_EMAIL, updateEmailTopic);
		topicMap.put(OtpPurpose.DELETE_ACCOUNT, deleteAccountTopic);
	}
	
	/* =====================================================
	 *                   SEND OTP
	 * ===================================================== */
	
	public void sendOtp(OtpPurpose purpose, String email, String otp) {
		String topic = topicMap.get(purpose);
		
		if (topic == null) {
			throw new IllegalStateException("Kafka topic not found for OTP purpose: " + purpose);
		}
		
		kafkaTemplate.send(topic, buildMessage(email, otp));
	}
	
	/* =====================================================
	 *                   BUILDER
	 * ===================================================== */
	
	private String buildMessage(String email, String otp) {
		return String.format(
			"{\"email\":\"%s\",\"otp\":\"%s\"}",
			email,
			otp
		);
	}
}
