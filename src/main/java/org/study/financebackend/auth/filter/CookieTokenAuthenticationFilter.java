package org.study.financebackend.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.filter.GenericFilterBean;
import org.study.financebackend.auth.service.AuthenticationService;

import java.io.IOException;
import java.util.Arrays;


public class CookieTokenAuthenticationFilter extends GenericFilterBean {

	private OpaqueTokenIntrospector tokenIntrospector;
	private AuthenticationService authenticationService;

	public CookieTokenAuthenticationFilter(OpaqueTokenIntrospector tokenIntrospector, AuthenticationService authenticationService) {
		this.tokenIntrospector = tokenIntrospector;
		this.authenticationService = authenticationService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = extractTokenFromCookie(httpServletRequest);
		if (token != null && !token.isEmpty() && !authenticationService.isTokenValid(token)) {
			String refreshToken = extractRefreshTokenFromCookie(httpServletRequest);
			String newAccessToken = authenticationService.refreshToken(refreshToken);
			if (newAccessToken != null) {
				OAuth2AuthenticatedPrincipal introspect = tokenIntrospector.introspect(newAccessToken);
				Authentication authentication = new UsernamePasswordAuthenticationToken(introspect, null, introspect.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				updateCookie((HttpServletResponse) response, newAccessToken);
				chain.doFilter(request, response);
			}
		}
		if (token != null && authenticationService.isTokenValid(token) && !token.isEmpty()) {
			OAuth2AuthenticatedPrincipal introspect = tokenIntrospector.introspect(token);
			Authentication authentication = new UsernamePasswordAuthenticationToken(introspect, null, introspect.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);


	}

	private void updateCookie(HttpServletResponse response, String accessToken) {
		Cookie accessCookie = new Cookie("code", accessToken);
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		accessCookie.setSecure(true);
		response.addCookie(accessCookie);
	}

	private String extractTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
					.filter(cookie -> "code".equals(cookie.getName()))
					.findFirst()
					.map(Cookie::getValue)
					.orElse(null);
		}
		return null;
	}

	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
					.filter(cookie -> "refreshCode".equals(cookie.getName()))
					.findFirst()
					.map(Cookie::getValue)
					.orElse(null);
		}
		return null;
	}
}
