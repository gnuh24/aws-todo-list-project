package aws.todolist.auth.service;

import aws.todolist.auth.dto.account.AccountCreateForm;
import aws.todolist.auth.dto.account.AccountRedisDTO;
import aws.todolist.auth.dto.auth.*;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.exceptionHandler.exceptions.DeleteConfirmationRequiredException;
import aws.todolist.auth.exceptionHandler.exceptions.jwtException.*;
import aws.todolist.auth.exceptionHandler.exceptions.loginException.AccountInactiveException;
import aws.todolist.auth.exceptionHandler.exceptions.loginException.AccountLockedException;
import aws.todolist.auth.exceptionHandler.exceptions.loginException.InvalidCredentialsException;
import aws.todolist.auth.exceptionHandler.exceptions.otpException.OtpNotFoundException;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorFailedException;
import aws.todolist.auth.exceptionHandler.exceptions.twoFactorException.TwoFactorRequiredException;
import aws.todolist.auth.integration.redis.RedisConstants;
import aws.todolist.auth.integration.redis.RedisService;
import aws.todolist.auth.mapper.AuthMapper;
import aws.todolist.auth.messaging.kafka.producer.KafkaProducerService;
import aws.todolist.auth.security.JwtTokenProvider;
import aws.todolist.auth.utils.IdGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	@Autowired
	private AuthMapper authMapper;
	
	@Autowired
	private TwoFactorService twoFactorService;
	
	@Override
	@Transactional
	public Account activeAccount(String otp) {
		
		AccountRedisDTO account = (AccountRedisDTO) redisService.get(RedisConstants.OTP_VERIFY_ACCOUNT + ":" + otp);
		
		if (account == null) {
			throw new OtpNotFoundException();
		}
		
		AccountCreateForm accountCreateForm = authMapper.toAccountCreateForm(account);
		
		redisService.set(RedisConstants.EMAIL_EXIST + ":" + accountCreateForm.getEmail(), "true");
		
		return accountService.saveAccount(accountCreateForm);
		
	}
	
	@Override
	public boolean isEmailExists(String email) {
		return redisService.exists(RedisConstants.EMAIL_EXIST + ":" + email);
	}
	
	@Override
	public AuthResponseDTO login(LoginRequestForm request) {
		Account account = accountService.getAccountByUsername(request.getEmail());
		
		if (account == null || !account.isEnabled() || !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
			throw new InvalidCredentialsException();
		}
		
		if (account.getStatus() == Account.Status.INACTIVE) {
			throw new AccountInactiveException();
		}
		
		if (account.getStatus() == Account.Status.BANNED) {
			throw new AccountLockedException();
		}

//		if (account.getRole() != Account.Role.USER) {
//			throw new BadCredentialsException("Email hoặc mật khẩu không đúng!");
//		}
		
		// Tạo và trả về AuthResponseDTO
		return authMapper.toAuthResponse(account, jwtTokenProvider);
	}
	
	@Override
	@Transactional
	public AuthResponseDTO loginGoogle(String email, String name, String avatar) {
		// 1️⃣ Tìm account theo email
		Account account = accountService.getAccountByUsername(email);
		
		// 2️⃣ Nếu chưa tồn tại → tạo mới
		if (account == null) {
			account = new Account();
			account.setId(UUID.randomUUID().toString());
			account.setEmail(email);
			
			// Tạo password ngẫu nhiên
			String randomPassword = UUID.randomUUID().toString().substring(0, 12);
			account.setPassword(passwordEncoder.encode(randomPassword));
			account.setDisplayName(name);
			account.setAvatar(avatar);
			account.setStatus(Account.Status.ACTIVE); // vì Google đã verify email
			account.setRole(Account.Role.USER);
			
			// Lưu DB
			accountService.saveAccount(account);
			redisService.set(RedisConstants.EMAIL_EXIST + ":" + account.getEmail(), "true");
			
		} else {
			// 3️⃣ User đã tồn tại → update thông tin nếu cần
			if (account.getAvatar() == null || !account.getAvatar().equals(avatar)) {
				account.setAvatar(avatar);
			}
			
			accountService.saveAccount(account);
		}
		
		
		if (account.getStatus() == Account.Status.BANNED) {
			throw new LockedException("Tài khoản của bạn đã bị khóa! Nếu có vấn đề, vui lòng liên hệ Admin.");
		}
		
		// 5️⃣ Trả về response (tạo JWT,...)
		AuthResponseDTO responseDTO = authMapper.toAuthResponse(account, jwtTokenProvider);
		responseDTO.setTokenExpirationTime("15 phút");
		responseDTO.setRefreshTokenExpirationTime("7 ngày");
		return responseDTO;
	}
	
	
	@Override
	public AuthResponseDTO staffLogin(LoginRequestForm request) {
		Account account = accountService.getAccountByUsername(request.getEmail());
		
		if (account == null || account.isEnabled() || account.getRole().equals(Account.Role.USER) || !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
			throw new BadCredentialsException("Email hoặc mật khẩu không đúng!");
		}
		
		if (account.getStatus().toString().equals("INACTIVE")) {
			throw new DisabledException("Tài khoản của bạn chưa được kích hoạt, hãy kiểm tra email " + request.getEmail());
		}
		
		if (account.getStatus().toString().equals("BANNED")) {
			throw new LockedException("Tài khoản của bạn đã bị khóa! Nếu có vấn đề, vui lòng liên hệ Admin.");
		}
		
		// Tạo và trả về AuthResponseDTO
		return authMapper.toAuthResponse(account, jwtTokenProvider);
	}
	
	
	@Override
	@Transactional
	public AccountRedisDTO register(UserRegistrationForm userRegistrationForm) {
//		if (accountService.isEmailExists(userRegistrationForm.getEmail())) {
//			throw new RuntimeException("Email :" + userRegistrationForm.getEmail() + " đã tồn tại trong hệ thống !");
//		}
		
		String accountId = UUID.randomUUID().toString();
		redisService.set(RedisConstants.EMAIL_EXIST + ":" + userRegistrationForm.getEmail(), "true", 5, TimeUnit.MINUTES);
		
		
		AccountRedisDTO account = new AccountRedisDTO();
		account.setId(accountId);
		account.setEmail(userRegistrationForm.getEmail());
		account.setPassword(
			passwordEncoder.encode(
				userRegistrationForm.getPassword()
			)
		);
		String otp = IdGenerator.generateOTP();
		redisService.setObjectWithTTL(RedisConstants.OTP_VERIFY_ACCOUNT + ":" + otp, account, 5, TimeUnit.MINUTES);
		
		kafkaProducerService.sendRegisterEmail(userRegistrationForm.getEmail(), otp);
		return account;
	}
	
	@Override
	public void sendOtpResetPassword(String email) {
		redisService.delete(RedisConstants.OTP_FORGOT_PASSWORD + ":" + email);
		String otp = IdGenerator.generateOTP();
		String key = RedisConstants.OTP_FORGOT_PASSWORD + ":" + email;
		redisService.set(key, otp, 3, TimeUnit.MINUTES);
		kafkaProducerService.sendResetPasswordEmail(email, otp);
	}
	
	@Override
	public Account resetPassword(String username, ResetPasswordForm form) {
		
		String key = RedisConstants.OTP_FORGOT_PASSWORD + ":" + username;
		
		Object otpObj = redisService.get(key);
		
		// 1. OTP không tồn tại (hết hạn hoặc chưa gửi)
		if (otpObj == null) {
			throw new OtpNotFoundException();
		}
		
		String otpRedis = otpObj.toString();
		
		// 2. OTP tồn tại nhưng sai
		if (!otpRedis.equals(form.getOtp())) {
			throw new OtpNotFoundException();
		}
		
		// 3. OTP đúng → xóa ngay (one-time)
		redisService.delete(key);
		
		// 4. Update password
		return accountService.updatePassword(username, form.getNewPassword());
	}
	
	
	@Override
	public Account updatePassword(String accountId, UpdatePasswordForm form) {
		
		Account account = accountService.getAccountById(accountId);
		
		if (!passwordEncoder.matches(form.getOldPassword(), account.getPassword())) {
			throw new TwoFactorFailedException("Mật khẩu hiện không đúng !!");
		}
		
		if (account.isTwoFactorEnabled()) {
			if (form.getTotp() == null) {
				throw new TwoFactorRequiredException(
					"Tài khoản của bạn đang bật xác thực 2 lớp (2FA). Vui lòng nhập mã xác thực từ ứng dụng."
				);
			}
			
			boolean validTotp = twoFactorService.verifyOtp(
				account.getTwoFactorSecret(),
				form.getTotp()
			);
			
			if (!validTotp) {
				throw new TwoFactorFailedException(
					"Mã xác thực 2FA không hợp lệ hoặc đã hết hạn. Vui lòng thử lại."
				);
			}
			
		}
		
	
		
		return accountService.updatePassword(account, form.getNewPassword());
		
	}
	
	@Override
	public void sendOtpUpdateEmail(String newEmail) {
		redisService.delete(RedisConstants.OTP_CHANGE_EMAIL + ":" + newEmail);
		String otp = IdGenerator.generateOTP();
		redisService.set(RedisConstants.OTP_CHANGE_EMAIL + ":" + newEmail, otp, 3, TimeUnit.MINUTES);
		kafkaProducerService.sendUpdateEmail(newEmail, otp);
	}
	
	@Override
	public void sendOtpDeleteAccount(String email) {
		
		String redisKey = RedisConstants.OTP_DELETE_ACCOUNT + ":" + email;
		
		// Clear OTP cũ (nếu có)
		redisService.delete(redisKey);
		
		// Generate OTP
		String otp = IdGenerator.generateOTP();
		
		// TTL 3 phút (giống update email)
		redisService.set(redisKey, otp, 3, TimeUnit.MINUTES);
		
		// Send OTP via Kafka (email)
		kafkaProducerService.sendDeleteAccount(email, otp);
	}
	
	@Override
	public Account updateEmail(String accountId, UpdateEmailForm form) {
		
		Account account = accountService.getAccountById(accountId);
		
		// 1️⃣ Validate password
		if (!passwordEncoder.matches(form.getCurrentPassword(), account.getPassword())) {
			throw new TwoFactorFailedException(
				"Mật khẩu hiện tại không đúng."
			);
		}
		
		// 2️⃣ Validate 2FA nếu bật
		if (account.isTwoFactorEnabled()) {
			
			if (form.getTotp() == null) {
				throw new TwoFactorRequiredException(
					"Tài khoản của bạn đang bật xác thực 2 lớp (2FA). Vui lòng nhập mã xác thực từ ứng dụng."
				);
			}
			
			boolean validTotp = twoFactorService.verifyOtp(
				account.getTwoFactorSecret(),
				form.getTotp()
			);
			
			if (!validTotp) {
				throw new TwoFactorFailedException(
					"Mã xác thực 2FA không hợp lệ hoặc đã hết hạn. Vui lòng thử lại."
				);
			}
		}
		
		// 3️⃣ Validate OTP email
		String redisKey = RedisConstants.OTP_CHANGE_EMAIL + ":" + form.getNewEmail();
		Object otpRedisObj = redisService.get(redisKey);
		
		if (otpRedisObj == null || !otpRedisObj.toString().equals(form.getOtp())) {
			throw new OtpNotFoundException();
		}
		
		// 4️⃣ Cleanup OTP
		redisService.delete(redisKey);
		
		// 5️⃣ Update email + cache
		String currentEmail = account.getUsername();
		redisService.delete(RedisConstants.EMAIL_EXIST + ":" + currentEmail);
		redisService.set(
			RedisConstants.EMAIL_EXIST + ":" + form.getNewEmail(),
			"true"
		);
		
		accountService.updateEmail(account, form.getNewEmail());
		
		return account;
	}
	
	
	@Override
	public AuthResponseDTO refreshToken(String refreshToken) {
		
		if (refreshToken == null || refreshToken.isEmpty()) {
			throw new RefreshTokenNotFoundException();
		}
		
		AuthResponseDTO response = new AuthResponseDTO();
		
		try {
			String typeToken = jwtTokenProvider.getTokenType(refreshToken);
			if (typeToken == null || !typeToken.equals("refresh")) {
				throw new InvalidTokenTypeException();
			}
			
			String emailFromRefreshToken = jwtTokenProvider.getUsername(refreshToken);
			
			
			//Tìm tài khoản dựa trên Email
			Account account = accountService.getAccountByUsername(emailFromRefreshToken);
			
			String redisKey = RedisConstants.BANLIST_ACCOUNT_ID + ":" + account.getId();
			
			if (redisService.get(redisKey) != null) {
				throw new RefreshTokenBlacklistedException();
			}
			
			
			response.setId(account.getId());
			response.setEmail(account.getEmail());
			response.setRole(account.getRole().toString());
			response.setDisplayName(account.getDisplayName());
			response.setAvatar(account.getAvatar());
			
			// Tạo Token
			String jwt = jwtTokenProvider.generateToken(account);
			response.setToken(jwt);
			response.setTokenExpirationTime("30 phút");
			
			// Tạo Refresh Token
			response.setRefreshToken(refreshToken);
			response.setRefreshTokenExpirationTime("7 ngày");
			
		} catch (RefreshTokenNotFoundException e) {
			throw new RefreshTokenNotFoundException();
		} catch (RefreshTokenBlacklistedException e) {
			throw new RefreshTokenBlacklistedException();
		} catch (ExpiredJwtException e) {
			throw new RefreshTokenExpiredException();
		} catch (SignatureException e) {
			throw new InvalidJWTSignatureException();
		} catch (MalformedJwtException e) {
			throw new RefreshTokenMalformedException();
		} catch (UnsupportedJwtException e) {
			throw new RefreshTokenUnsupportedException();
		} catch (UsernameNotFoundException e) {
			throw new RefreshTokenUnknownSubjectException();
		} catch (InvalidTokenTypeException e) {
			throw new InvalidTokenTypeException();
		} catch (Exception e) {
			throw new RefreshTokenUnknownErrorException();
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public Account deleteAccount(String accountId, DeleteAccountForm form) {
		
		Account account = accountService.getAccountById(accountId);
		
		/* =====================================================
		 * 1. Confirm text
		 * ===================================================== */
		String expectedConfirm = "delete";
		if (form.getConfirmText() == null ||
			!expectedConfirm.equalsIgnoreCase(form.getConfirmText().trim())) {
			throw new DeleteConfirmationRequiredException();
		}
		
		/* =====================================================
		 * 2. Verify password
		 * ===================================================== */
		if (!passwordEncoder.matches(form.getPassword(), account.getPassword())) {
			throw new TwoFactorFailedException("Mật khẩu không đúng.");
		}
		
		/* =====================================================
		 * 3. Verify OTP (default – chưa dùng TOTP)
		 * ===================================================== */
		String redisDeleteOtpKey =
			RedisConstants.OTP_DELETE_ACCOUNT + ":" + account.getEmail();
		
		Object otpObj = redisService.get(redisDeleteOtpKey);
		
		// OTP không tồn tại (chưa gửi hoặc đã hết hạn)
		if (otpObj == null) {
			throw new OtpNotFoundException();
		}
		
		String otpRedis = otpObj.toString();
		
		// OTP không khớp
		if (!otpRedis.equals(form.getOtp())) {
			throw new OtpNotFoundException();
		}
		
		// Clear OTP sau khi verify thành công
		redisService.delete(redisDeleteOtpKey);
		
		/* =====================================================
		 * 4. Revoke token (force logout)
		 * ===================================================== */
		String redisBanlistAccountIdKey =
			RedisConstants.BANLIST_ACCOUNT_ID + ":" + account.getId();
		redisService.set(redisBanlistAccountIdKey, true);
		
		/* =====================================================
		 * 5. Soft delete account
		 * ===================================================== */
		return accountService.deleteAccount(account);
	}
	
}
