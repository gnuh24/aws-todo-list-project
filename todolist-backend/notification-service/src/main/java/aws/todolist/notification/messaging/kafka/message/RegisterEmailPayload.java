package aws.todolist.notification.messaging.kafka.message;

public class RegisterEmailPayload {
    private String email;
    private String otp;

    // Constructors, getters, setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}