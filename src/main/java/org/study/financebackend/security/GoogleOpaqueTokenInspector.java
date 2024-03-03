package org.study.financebackend.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.study.financebackend.model.UserModel;

import java.util.HashMap;
import java.util.Map;


public class GoogleOpaqueTokenInspector implements OpaqueTokenIntrospector {

	private final WebClient userInfoClient;
	public GoogleOpaqueTokenInspector(WebClient userInfoClient) {
		this.userInfoClient = userInfoClient;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		UserInfo userInfo = userInfoClient.get()
				.uri(uriBuilder -> uriBuilder.path("/oauth2/v3/userinfo").queryParam("access_token", token).build())
				.retrieve()
				.bodyToMono(UserInfo.class)
				.block();

		Map<String,Object> attributes = new HashMap<>();
		attributes.put("sub",userInfo.sub());
		attributes.put("given_name",userInfo.given_name());
		attributes.put("picture",userInfo.picture());
		attributes.put("email",userInfo.email());
		return new OAuth2IntrospectionAuthenticatedPrincipal(userInfo.name(),attributes,null);
	}
}
