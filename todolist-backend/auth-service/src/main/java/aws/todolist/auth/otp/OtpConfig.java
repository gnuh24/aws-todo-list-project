package aws.todolist.auth.otp;

public record OtpConfig(
    String redisPrefix,
    long ttlMinutes
) {}
