package aws.todolist.auth.otp;

import aws.todolist.auth.integration.redis.RedisConstants;
import aws.todolist.auth.integration.redis.RedisService;
import aws.todolist.auth.messaging.kafka.producer.KafkaProducerService;
import aws.todolist.auth.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisService redisService;
    private final KafkaProducerService kafkaProducerService;

    private static final Map<OtpPurpose, String> REDIS_PREFIX = new EnumMap<>(OtpPurpose.class);

    static {
        REDIS_PREFIX.put(OtpPurpose.VERIFY_ACCOUNT, RedisConstants.OTP_VERIFY_ACCOUNT);
        REDIS_PREFIX.put(OtpPurpose.FORGOT_PASSWORD, RedisConstants.OTP_FORGOT_PASSWORD);
        REDIS_PREFIX.put(OtpPurpose.CHANGE_EMAIL, RedisConstants.OTP_CHANGE_EMAIL);
        REDIS_PREFIX.put(OtpPurpose.DELETE_ACCOUNT, RedisConstants.OTP_DELETE_ACCOUNT);
    }

    @Override
    public void sendOtp(OtpPurpose purpose, String emailTarget) {

        String redisKey = buildRedisKey(purpose, emailTarget);

        // Clear old OTP
        redisService.delete(redisKey);

        // Generate OTP
        String otp = IdGenerator.generateOTP();

        // Save OTP (TTL = 3 minutes)
        redisService.set(redisKey, otp, 3, TimeUnit.MINUTES);

        // Send OTP via Kafka
        kafkaProducerService.sendOtp(purpose, emailTarget, otp);
    }
	

    private String buildRedisKey(OtpPurpose purpose, String emailTarget) {
        String prefix = REDIS_PREFIX.get(purpose);
        if (prefix == null) {
            throw new IllegalStateException("Redis prefix not found for OTP purpose: " + purpose);
        }
        return prefix + ":" + emailTarget;
    }
}
