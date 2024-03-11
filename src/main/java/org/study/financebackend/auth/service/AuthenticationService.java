package org.study.financebackend.auth.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
@Slf4j
@Component

public class AuthenticationService {

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
	private String clientSecret;
	private static final String URL_REDIRECT = "https://financial-tracker-frontend.onrender.com";


	public String getUrlForLogin() {
		String url = new GoogleAuthorizationCodeRequestUrl(
				clientId,
				URL_REDIRECT,
				Arrays.asList("email", "profile", "openid")
		).setAccessType("offline").setApprovalPrompt("force").build();

		log.info("get auth url {}", url);
		return url;
	}

	public void callback(String code, HttpServletResponse response) throws IOException {
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
		response.addHeader("Set-Cookie", String.format("code=%s; HttpOnly; Secure; SameSite=None; Path=/", accessToken));
		response.addHeader("Set-Cookie", String.format("refreshCode=%s; HttpOnly; Secure; SameSite=None; Path=/", refreshToken));
		log.info("set token");
	}

	public void logout(HttpServletResponse response){
		response.addHeader("Set-Cookie", String.format("code=%s; HttpOnly; Secure; SameSite=None; Path=/", ""));
		response.addHeader("Set-Cookie", String.format("refreshCode=%s; HttpOnly; Secure; SameSite=None; Path=/", ""));
		log.info("delete token");
	}


	public boolean isTokenValid(String accessToken) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String tokenInfoUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken;

			Map<String, Object> response = restTemplate.getForObject(tokenInfoUrl, Map.class);

			return response != null && response.containsKey("email");
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public String refreshToken(String refreshToken) throws IOException {
		GoogleTokenResponse response = new GoogleRefreshTokenRequest(
				new NetHttpTransport(),
				new GsonFactory(),
				refreshToken,
				clientId,
				clientSecret
		).execute();

		return response.getAccessToken();
	}
}
