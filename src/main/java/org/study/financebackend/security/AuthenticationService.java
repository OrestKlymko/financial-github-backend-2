package org.study.financebackend.security;


import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class AuthenticationService {

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
	private String clientSecret;

	public boolean isTokenValid(String accessToken) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String tokenInfoUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken;

			Map<String, Object> response = restTemplate.getForObject(tokenInfoUrl, Map.class);

			return response != null && response.containsKey("email");
		} catch (Exception e) {
			// У випадку помилки (наприклад, якщо токен недійсний і Google повертає помилку), повертаємо false
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

		return response.getAccessToken(); // Отриманий новий access token
	}
}
