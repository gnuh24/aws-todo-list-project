package aws.todolist.auth.security;

import aws.todolist.auth.dto.auth.AuthResponseDTO;
import aws.todolist.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
	
	@Value("${domain.frontend}")
	private String domainFrontEnd;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication) throws IOException {
		
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
		
		String email = (String) attributes.get("email");
		String name = (String) attributes.get("name");
		String avatar = (String) attributes.get("picture");
		
		AuthResponseDTO loginInfo = authService.loginGoogle(email, name, avatar);
		
		System.err.println("Info: " + loginInfo);
		
		// Build query string
		String redirectUrl = String.format(
			domainFrontEnd
				+ "/app/inbox"
				+ "?id=%s"
				+ "&email=%s"
				+ "&displayName=%s"
				+ "&avatar=%s"
				+ "&role=%s"
				+ "&token=%s"
				+ "&tokenExpirationTime=%s"
				+ "&refreshToken=%s"
				+ "&refreshTokenExpirationTime=%s",
			
			URLEncoder.encode(loginInfo.getId(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getEmail(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getDisplayName(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getAvatar(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getRole(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getToken(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getTokenExpirationTime(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getRefreshToken(), StandardCharsets.UTF_8),
			URLEncoder.encode(loginInfo.getRefreshTokenExpirationTime(), StandardCharsets.UTF_8)
		);
		
		System.err.println("Redic: " + redirectUrl);
		
		
		response.sendRedirect(redirectUrl);
	}
	
}
