package aws.todolist.auth.service;

import aws.todolist.auth.dto.twoFactor.TwoFactorSetupResponse;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorException;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorFailedException;
import aws.todolist.auth.integration.redis.RedisConstants;
import aws.todolist.auth.integration.redis.RedisService;
import aws.todolist.auth.service.AccountService;
import aws.todolist.auth.service.TwoFactorService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {
	
	private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
	private final RedisService redisService;
	private final AccountService accountService;
	
	@Autowired
	public TwoFactorServiceImpl(RedisService redisService, AccountService accountService) {
		this.redisService = redisService;
		this.accountService = accountService;
	}
	
	@Override
	public TwoFactorSetupResponse setup2FA(String email) {
		GoogleAuthenticatorKey key = gAuth.createCredentials();
		
		String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
			"AWS Todolist",
			email,
			key
		).replace("margin=0", "margin=10");
		
		
		// Lưu secret tạm thời trong Redis 10 phút
		redisService.set(RedisConstants.TWO_FA_PENDING_SECRET + ":" + email, key.getKey(), 10, TimeUnit.MINUTES);
		
		TwoFactorSetupResponse resp = new TwoFactorSetupResponse();
		resp.setQrUrl(qrUrl);
		resp.setSecret(key.getKey());
		return resp;
	}
	
	@Override
	public void verify2FA(String accountId, int otp) {
		Account account = accountService.getAccountById(accountId);
		Object secretObj = redisService.get( RedisConstants.TWO_FA_PENDING_SECRET + ":" + account.getEmail());
		if (secretObj == null) {
			throw new TwoFactorFailedException("2FA secret đã hết hạn hoặc chưa setup");
		}

		String secret = secretObj.toString();
		if (!gAuth.authorize(secret, otp)) {
			throw new TwoFactorFailedException("OTP không hợp lệ");
		}

		// Gắn secret vào account, enable 2FA
		account.setTwoFactorSecret(secret);
		account.setTwoFactorEnabled(true);
		account.setTwoFactorVerifiedAt(LocalDateTime.now());
		accountService.saveAccount(account);

		// Xóa secret tạm
		redisService.delete("2FA_PENDING_SECRET:" + account.getEmail());
	}
	
	@Override
	public void disable2FA(String accountId, int otp) {
		Account account = accountService.getAccountById(accountId);
		
		// Chưa bật 2FA mà đòi tắt
		if (!account.isTwoFactorEnabled() || account.getTwoFactorSecret() == null) {
			throw new TwoFactorFailedException("Tài khoản chưa bật 2FA");
		}
		
		String secret = account.getTwoFactorSecret();
		
		// Verify OTP bằng secret đang lưu trong DB
		if (!gAuth.authorize(secret, otp)) {
			throw new TwoFactorFailedException("OTP không hợp lệ");
		}
		
		// Disable 2FA
		account.setTwoFactorEnabled(false);
		account.setTwoFactorSecret(null);
		account.setTwoFactorVerifiedAt(null);
		
		accountService.saveAccount(account);
	}
	
	
}