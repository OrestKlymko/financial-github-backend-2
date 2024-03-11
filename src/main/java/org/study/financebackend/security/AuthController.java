package org.study.financebackend.security;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;

@RestController
@Slf4j
public class AuthController {

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
	private String clientSecret;

	private static final String URL_REDIRECT="https://financial-tracker-frontend.onrender.com";
//	private static final String URL_REDIRECT="https://financial-tracker-frontend.vercel.app";
//	private static final String URL_REDIRECT="http://localhost:4200";


	@GetMapping("/auth/url")
	public ResponseEntity<UrlDto> auth() {
		String url = new GoogleAuthorizationCodeRequestUrl(
				clientId,
				URL_REDIRECT,
				Arrays.asList("email", "profile", "openid")
		).setAccessType("offline").setApprovalPrompt("force").build();

		log.info("get auth url {}",url);
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
				URL_REDIRECT
		).execute();
		String accessToken = execute.getAccessToken();
		String refreshToken = execute.getRefreshToken();
		System.out.println("accessToken = " + accessToken);
		response.addHeader("Set-Cookie", String.format("code=%s; HttpOnly; Secure; SameSite=Strict; Path=/", accessToken));
		response.addHeader("Set-Cookie", String.format("refreshCode=%s; HttpOnly; Secure; SameSite=Strict; Path=/", refreshToken));
		log.info("set token");
		return ResponseEntity.ok().build();
	}

	@GetMapping("/user/logout")
	public void logout(HttpServletResponse response) {
		response.addHeader("Set-Cookie", String.format("code=%s; HttpOnly; Secure; SameSite=Strict; Path=/", ""));
		response.addHeader("Set-Cookie", String.format("refreshCode=%s; HttpOnly; Secure; SameSite=Strict; Path=/", ""));
		log.info("delete token");
	}

}
