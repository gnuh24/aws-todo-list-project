package aws.todolist.auth.utils;

import aws.todolist.auth.entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
	
	private SecurityUtils() {
		// Ngăn không cho khởi tạo
	}
	
	/**
	 * Lấy thông tin Authentication hiện tại từ SecurityContext.
	 */
	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	/**
	 * Lấy Account hiện tại từ SecurityContext (nếu có).
	 */
	public static Account getCurrentAccount() {
		Authentication auth = getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Account account) {
			return account;
		}
		return null;
	}
	
	/**
	 * Lấy username hiện tại — nếu chưa đăng nhập thì trả về "Anonymous".
	 */
	public static String getCurrentUsername() {
		Account account = getCurrentAccount();
		return (account != null) ? account.getUsername() : "Anonymous";
	}
	
	/**
	 * Kiểm tra user hiện tại có vai trò cụ thể hay không.
	 */
	public static boolean hasRole(String role) {
		Authentication auth = getAuthentication();
		if (auth == null || auth.getAuthorities() == null) return false;
		
		return auth.getAuthorities().stream()
		    .anyMatch(granted -> granted.getAuthority().equals(role));
	}
}