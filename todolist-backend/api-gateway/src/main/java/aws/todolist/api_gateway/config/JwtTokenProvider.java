package aws.todolist.api_gateway.config;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Component
@Data
public class JwtTokenProvider {
	
	@Value("${jwt.secret}")
	private String secretString;
	
	private SecretKey secretKey;  // Use a single secret key for both access and refresh tokens
	
//	private static final long EXPIRATION_TIME_FOR_TOKEN = 0;
//	private static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 0;
	
	private static final long EXPIRATION_TIME_FOR_TOKEN = 30L * 24 * 60 * 60 * 1000;
	private static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 30L * 24 * 60 * 60 * 1000;
	private static final long INTERNAL_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;
	
	@PostConstruct
	public void init() {
		if (secretString == null || secretString.isBlank()) {
			throw new IllegalStateException("JWT secret key is not set in application.properties");
		}
		byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
		this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
	}
	
	
	public void logJwtTokenInfo(String token) {
		String[] parts = token.split("\\.");
		String encodedPayload = parts[1];
		String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
		
		System.err.println("===== JWT Token Info =====");
		System.err.println(payload);
		System.err.println("==========================");
	}
	
	
	public String getUsername(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
	}
	
	public String getTokenType(String token) {
		String[] parts = token.split("\\.");
		String encodedPayload = parts[1];
		String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
		
		// Tìm chuỗi "typ":"xxx"
		int typIndex = payload.indexOf("\"typ\"");
		if (typIndex == -1) return null;
		
		int colonIndex = payload.indexOf(":", typIndex);
		int firstQuote = payload.indexOf("\"", colonIndex + 1);
		int secondQuote = payload.indexOf("\"", firstQuote + 1);
		
		return payload.substring(firstQuote + 1, secondQuote);
	}
	
	
	public String getAccountId(String token) {
		String[] parts = token.split("\\.");
		String encodedPayload = parts[1];
		String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
		
		int keyIndex = payload.indexOf("\"accountId\"");
		if (keyIndex == -1) return null;
		
		int colonIndex = payload.indexOf(":", keyIndex);
		int firstQuote = payload.indexOf("\"", colonIndex + 1);
		int secondQuote = payload.indexOf("\"", firstQuote + 1);
		
		return payload.substring(firstQuote + 1, secondQuote);
	}
	
	public String getRole(String token) {
		String[] parts = token.split("\\.");
		String encodedPayload = parts[1];
		String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
		
		int keyIndex = payload.indexOf("\"role\"");
		if (keyIndex == -1) return null;
		
		int colonIndex = payload.indexOf(":", keyIndex);
		int firstQuote = payload.indexOf("\"", colonIndex + 1);
		int secondQuote = payload.indexOf("\"", firstQuote + 1);
		
		return payload.substring(firstQuote + 1, secondQuote);
	}
	
	
	
	// Extract username from JWT Token without using library methods (manual extraction)
	public String getUsernameWithoutExpired(String token) {
		String[] parts = token.split("\\.");
		String encodedPayload = parts[1];
		String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
		return payload.split("\"")[3];
	}
	
	
}
