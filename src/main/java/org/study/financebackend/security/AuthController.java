package org.study.financebackend.security;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;

@RestController
//@CrossOrigin(allowCredentials = "true", originPatterns = "*")
public class AuthController {

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
	private String clientSecret;


	@GetMapping("/auth/url")
	public ResponseEntity<UrlDto> auth() {
		String url = new GoogleAuthorizationCodeRequestUrl(
				clientId,
				"https://financial-tracker-frontend.onrender.com/",
				Arrays.asList("email", "profile", "openid")
		).setAccessType("offline").setApprovalPrompt("force").build();
		return ResponseEntity.ok(new UrlDto(url));
	}

	@GetMapping("/auth/callback")
	public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {

		GoogleTokenResponse execute = new GoogleAuthorizationCodeTokenRequest(
				new NetHttpTransport(),
				new GsonFactory(),
				clientId,
				clientSecret,
				code,
				"https://financial-tracker-frontend.onrender.com/"
		).execute();
		String accessToken = execute.getAccessToken();
		String refreshToken = execute.getRefreshToken();
//		Cookie accessCookie = new Cookie("code", accessToken);
//		Cookie refreshCookie = new Cookie("refreshCode", refreshToken);
//		accessCookie.setHttpOnly(true);
//		accessCookie.setPath("/");
//		accessCookie.setSecure(true);
//
//
//		response.setHeader("Set-Cookie", String.format("code=%s; HttpOnly; SameSite=none",accessCookie));
//		response.setHeader("Set-Cookie",String.format("refreshCode=%s; HttpOnly; SameSite=none",refreshCookie));
//
//
//		refreshCookie.setHttpOnly(true);
//		refreshCookie.setPath("/");
//		refreshCookie.setSecure(true);
//		response.addCookie(accessCookie);
//		response.addCookie(refreshCookie);

		response.setHeader("Set-Cookie", String.format("code=%s; HttpOnly; Secure; SameSite=None",accessToken));
		response.setHeader("Set-Cookie",String.format("refreshCode=%s; HttpOnly; Secure; SameSite=None",refreshToken));
		return ResponseEntity.ok().build();
	}
}
