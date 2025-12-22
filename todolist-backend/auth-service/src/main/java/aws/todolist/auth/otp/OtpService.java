package aws.todolist.auth.otp;

public interface OtpService {
	
	void sendOtp(OtpPurpose purpose, String target);
	
}
