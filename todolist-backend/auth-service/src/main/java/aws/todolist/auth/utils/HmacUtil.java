package aws.todolist.auth.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacUtil {
    private static final String SECRET_KEY = "real-secret-key";

    public static String generateHMAC(String body) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);

        byte[] hash = sha256_HMAC.doFinal(body.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
	
	// Verify HMAC (check chữ ký)
	public static boolean verifyHMAC(String body, String providedSignature) throws Exception {
		String expectedSignature = generateHMAC(body);
		return expectedSignature.equals(providedSignature);
	}

}
