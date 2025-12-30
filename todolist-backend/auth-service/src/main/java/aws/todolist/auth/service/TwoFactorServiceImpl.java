package aws.todolist.auth.service;

import aws.todolist.auth.dto.twoFactor.TwoFactorDisableForm;
import aws.todolist.auth.dto.twoFactor.TwoFactorSetupResponse;
import aws.todolist.auth.dto.twoFactor.TwoFactorVeriyResponse;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorFailedException;
import aws.todolist.auth.integration.redis.RedisConstants;
import aws.todolist.auth.integration.redis.RedisService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {
	
	private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
	private final RedisService redisService;
	private final AccountService accountService;
	private final AccountRecoveryKeyService accountRecoveryKeyService;
	
	@Autowired
	public TwoFactorServiceImpl(
		RedisService redisService,
		AccountService accountService,
		AccountRecoveryKeyService accountRecoveryKeyService
	) {
		this.redisService = redisService;
		this.accountService = accountService;
		this.accountRecoveryKeyService = accountRecoveryKeyService;
	}
	
	
	@Override
	public TwoFactorSetupResponse setup2FA(String email) {
		GoogleAuthenticatorKey key = gAuth.createCredentials();
		
		String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
			"AWS Todolist Dev",
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
	public TwoFactorVeriyResponse verify2FA(String accountId, int otp) {
		
		Account account = accountService.getAccountById(accountId);
		
		Object secretObj = redisService.getObject(
			RedisConstants.TWO_FA_PENDING_SECRET + ":" + account.getEmail()
		);
		
		if (secretObj == null) {
			throw new TwoFactorFailedException("2FA secret đã hết hạn hoặc chưa setup");
		}
		
		String secret = secretObj.toString();
		
		if (!gAuth.authorize(secret, otp)) {
			throw new TwoFactorFailedException("OTP không hợp lệ");
		}
		
		// Enable 2FA
		account.setTwoFactorSecret(secret);
		account.setTwoFactorEnabled(true);
		account.setTwoFactorVerifiedAt(LocalDateTime.now());
		accountService.saveAccount(account);
		
		// Cleanup redis
		redisService.delete(RedisConstants.TWO_FA_PENDING_SECRET + ":" + account.getEmail());
		
		// 🔑 Generate recovery keys
		List<String> recoveryKeys = accountRecoveryKeyService.generateRecoveryKeys(account.getId());
		
		// Trả recovery keys về FE (1 lần duy nhất)
		TwoFactorVeriyResponse resp = new TwoFactorVeriyResponse();
		resp.setRecoveryKeys(recoveryKeys);
		
		return resp;
	}
	
	
	@Override
	@Transactional
	public void disable2FA(String accountId, TwoFactorDisableForm form) {
		
		Account account = accountService.getAccountById(accountId);
		
		// 1️⃣ Check trạng thái
		if (!account.isTwoFactorEnabled() || account.getTwoFactorSecret() == null) {
			throw new TwoFactorFailedException("Tài khoản chưa bật 2FA");
		}
		
		boolean verified = false;
		
		// 2️⃣ Ưu tiên OTP
		if (form.getOtp() > 0) {
			verified = gAuth.authorize(
				account.getTwoFactorSecret(),
				form.getOtp()
			);
		}
		
		// 3️⃣ Fallback sang recovery key
		if (!verified && form.getRecoveryKey() != null && !form.getRecoveryKey().isBlank()) {
			verified = accountRecoveryKeyService.useRecoveryKey(
				accountId,
				form.getRecoveryKey()
			);
		}
		
		// 4️⃣ Fail cả hai
		if (!verified) {
			throw new TwoFactorFailedException("OTP hoặc Recovery Key không hợp lệ");
		}
		
		// 5️⃣ Disable 2FA
		account.setTwoFactorEnabled(false);
		account.setTwoFactorSecret(null);
		account.setTwoFactorVerifiedAt(null);
		accountService.saveAccount(account);
		
		// 6️⃣ 🔥 Revoke toàn bộ recovery key
		accountRecoveryKeyService.deleteAllByAccountId(accountId);
	}
	
	
	
	@Override
	public boolean verifyOtp(String secret, int totp) {
		if (secret == null || secret.isBlank()) {
			return false;
		}
		return gAuth.authorize(secret, totp);
	}
	
	
}