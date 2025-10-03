package aws.todolist.auth.service;

import aws.todolist.auth.aop.AppLogger;
import aws.todolist.auth.dto.account.AccountCreateForm;
import aws.todolist.auth.dto.account.AccountRedisDTO;
import aws.todolist.auth.dto.auth.*;
import aws.todolist.auth.exceptions.JwtException.*;
import aws.todolist.auth.entity.Account;
import aws.todolist.auth.exceptions.AuthException.AuthExceptionHandler;
import aws.todolist.auth.exceptions.AuthException.StepUpAuthenticationException;
import aws.todolist.auth.exceptions.otpException.OtpNotFoundException;
import aws.todolist.auth.integration.redis.RedisConstants;
import aws.todolist.auth.integration.redis.RedisService;
import aws.todolist.auth.security.JwtTokenProvider;
import aws.todolist.auth.utils.EnvironmentUtils;
import aws.todolist.auth.utils.IdGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private ModelMapper modelMapper;
	
	@Autowired
	private AppLogger log;
	
	@Autowired
	private EnvironmentUtils environmentUtils;
	
	@Autowired
	private AuthExceptionHandler authExceptionHandler;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private RedisService redisService;
	
	
	
	@Override
	@Transactional
	public Account activeAccount(String otp) {
		
		AccountRedisDTO account = (AccountRedisDTO) redisService.get(RedisConstants.OTP_VERIFY_ACCOUNT + ":" + otp);
		
		if (account == null) {
			throw new OtpNotFoundException("OTP không tồn tại hoặc đã hết hạn sử dụng !");
		}
		
		AccountCreateForm accountCreateForm = new AccountCreateForm();
		accountCreateForm.setId(account.getId());
		accountCreateForm.setEmail(account.getEmail());
		accountCreateForm.setPassword(account.getPassword());
		accountCreateForm.setAvatar("avatar-default-icon.png");
		
		redisService.set(RedisConstants.EMAIL_EXIST + ":" + accountCreateForm.getEmail(), "true");
		
		return accountService.createAccount(accountCreateForm);
		
	}
	
	@Override
	public boolean isEmailExists(String email) {
		return redisService.exists(RedisConstants.EMAIL_EXIST + ":" + email);
	}
	
	@Override
	public AuthResponseDTO login(LoginRequestForm request) {
		Account user = accountService.getAccountByUsername(request.getEmail());
		
		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Email hoặc mật khẩu không đúng!");
		}
		
		if (user.getRole() != Account.Role.USER) {
			throw new BadCredentialsException("Email hoặc mật khẩu không đúng!");
		}
		
		if (user.getStatus().toString().equals("INACTIVE")) {
			throw new DisabledException("Tài khoản của bạn chưa được kích hoạt, hãy kiểm tra email " + request.getEmail());
		}
		
		if (user.getStatus().toString().equals("BANNED")) {
			throw new LockedException("Tài khoản của bạn đã bị khóa! Nếu có vấn đề, vui lòng liên hệ Admin.");
		}
		
		// Tạo và trả về AuthResponseDTO
		return buildAuthResponse(user);
	}
	
	@Override
	public AuthResponseDTO staffLogin(LoginRequestForm request) {
		Account user = accountService.getAccountByUsername(request.getEmail());
		
		if (user == null || user.getRole().equals(Account.Role.USER) || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Email hoặc mật khẩu không đúng!");
		}
		
		if (user.getStatus().toString().equals("INACTIVE")) {
			throw new DisabledException("Tài khoản của bạn chưa được kích hoạt, hãy kiểm tra email " + request.getEmail());
		}
		
		if (user.getStatus().toString().equals("BANNED")) {
			throw new LockedException("Tài khoản của bạn đã bị khóa! Nếu có vấn đề, vui lòng liên hệ Admin.");
		}
		
		// Tạo và trả về AuthResponseDTO
		return buildAuthResponse(user);
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
		
		emailService.sendRegistrationUserConfirm(userRegistrationForm.getEmail(), otp);
		return account;
	}
	
	@Override
	public void sendOtpResetPassword(String username) {
		redisService.delete(RedisConstants.OTP_FORGOT_PASSWORD + ":" + username);
		String otp = IdGenerator.generateOTP();
		redisService.set(RedisConstants.OTP_FORGOT_PASSWORD + ":" + username, otp, 3, TimeUnit.MINUTES);
		emailService.sendResetPasswordUserConfirm(username, otp);
	}
	
	@Override
	public Account resetPassword(String username, ResetPasswordForm form) {
		String otpRedis = redisService.get(RedisConstants.OTP_FORGOT_PASSWORD + ":" + username).toString();
		
		if (!otpRedis.equals(form.getOtp())) {
			throw new OtpNotFoundException("OTP không hợp lệ hoặc đã hết hạn!");
		}
		
		redisService.delete(RedisConstants.OTP_FORGOT_PASSWORD + ":" + username);
		return accountService.updatePassword(username, form.getNewPassword());
	}
	
	@Override
	public Account updatePassword(UpdatePasswordForm form) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		if (!passwordEncoder.matches(form.getOldPassword(), account.getPassword())) {
			throw new StepUpAuthenticationException("Mật khẩu hiện không đúng !!");
		}
		
		String newPassword = passwordEncoder.encode(form.getNewPassword());
		return accountService.updatePassword(account, newPassword);
		
	}
	
	@Override
	public void sendOtpUpdateEmail(String newEmail) {
		redisService.delete(RedisConstants.OTP_CHANGE_EMAIL + ":" + newEmail);
		String otp = IdGenerator.generateOTP();
		redisService.set(RedisConstants.OTP_CHANGE_EMAIL + ":" + newEmail, otp, 3, TimeUnit.MINUTES);
		emailService.sendUpdateEmailOtp(newEmail, otp);
	}
	
	@Override
	public Account updateEmail(UpdateEmailForm form) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		if (!passwordEncoder.matches(form.getCurrentPassword(), account.getPassword())) {
			throw new StepUpAuthenticationException("Mật khẩu hiện tại không đúng.");
		}
		
		String otpRedis = redisService.get(RedisConstants.OTP_CHANGE_EMAIL + ":" + form.getNewEmail()).toString();
		if (!otpRedis.equals(form.getOtp())) {
			throw new OtpNotFoundException("OTP không hợp lệ hoặc đã hết hạn!");
		}
		
		redisService.delete(RedisConstants.OTP_CHANGE_EMAIL + ":" + form.getNewEmail());

		
		String currentEmail = account.getUsername();
		redisService.delete(RedisConstants.EMAIL_EXIST + ":" + currentEmail);
		redisService.set(RedisConstants.EMAIL_EXIST + ":" + form.getNewEmail(), "true");
		
		accountService.updateEmail(account, form.getNewEmail());
		
//		redisService.set(RedisConstants.BANLIST_ACCESS_TOKEN + ":" + form.getAccessToken(), "true",15, TimeUnit.MINUTES);
//		redisService.set(RedisConstants.BANLIST_REFRESH_TOKEN + ":" + form.getRefreshToken(), "true",7, TimeUnit.DAYS);
//
		return account;
	}
	
	
	private AuthResponseDTO buildAuthResponse(Account user) {
		AuthResponseDTO response = new AuthResponseDTO();
		response.setId(user.getId());
		response.setEmail(user.getUsername());
		response.setRole(user.getRole().toString());
		response.setDisplayName(user.getDisplayName());
		response.setAvatar(user.getAvatar());
		// Tạo Token
		String jwt = jwtTokenProvider.generateToken(user);
		response.setToken(jwt);
		response.setTokenExpirationTime("30 phút");

//		redisService.set(RedisContants.TOKEN + jwt, true);
		
		
		// Tạo Refresh Token
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);
		response.setRefreshToken(refreshToken);
		response.setRefreshTokenExpirationTime("7 ngày");
		
		return response;
	}
	
	@Override
	public AuthResponseDTO refreshToken(HttpServletRequest request) {
		String refreshToken = "";
		
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("refresh_token".equals(cookie.getName())) {
					refreshToken = cookie.getValue(); // ✅ This is your refresh token
				}
			}
		}
		
		if (refreshToken.isEmpty()) {
			throw new RefreshTokenNotFound("Không tìm thấy refresh token");
		}
		
		AuthResponseDTO response = new AuthResponseDTO();
		String errorString = "Token không hợp lệ hoặc đã hết hạn sử dụng.";
		
		try {
			String typeToken = jwtTokenProvider.getTokenType(refreshToken);
			if (typeToken == null || !typeToken.equals("refresh")) {
				throw new InvalidTokenTypeException("Token có type không hợp lệ.");
			}
			
			String emailFromRefreshToken = jwtTokenProvider.getUsername(refreshToken);
			
			//Tìm tài khoản dựa trên Email
			Account account = accountService.getAccountByUsername(emailFromRefreshToken);
			
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
			
		} catch (ExpiredJwtException e) {
			throw new RefreshTokenExpiredException(errorString);
			
		} catch (SignatureException e) {
			throw new InvalidJWTSignatureException(errorString);
			
		} catch (UsernameNotFoundException e) {
			throw new UsernameNotFound(errorString);
			
		} catch (InvalidTokenTypeException e) {
			throw new InvalidTokenTypeException(errorString);
		}
		
		return response;
	}
	
	
}
