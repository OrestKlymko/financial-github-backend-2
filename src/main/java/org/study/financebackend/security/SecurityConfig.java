package org.study.financebackend.security;


import io.netty.handler.codec.http.cors.CorsConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

	private final WebClient userInfoClient;
	private final AuthenticationService service;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, OpaqueTokenIntrospector introspector) throws Exception {

		return httpSecurity
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling(customizer -> customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer(c -> c.opaqueToken(Customizer.withDefaults()))
				.authorizeHttpRequests(http -> http
						.requestMatchers("/auth/**").permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(new CookieTokenAuthenticationFilter(introspector, service), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList("https://financial-tracker-frontend.onrender.com","http://localhost:4200","http://localhost:8080","https://financial-github-backend-production.up.railway.app","https://microservice-production-c110.up.railway.app/"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "X-CSRF-Token", "Host", "Content-Length", "accept", "Connection"));
		configuration.setAllowCredentials(true); // Щоб дозволити відправлення кукі через перехресні запити
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Застосувати конфігурацію до всіх шляхів
		log.info("Cors configuration setup");
		return source;
	}

	@Bean
	public OpaqueTokenIntrospector introspector() {
		return new GoogleOpaqueTokenInspector(userInfoClient);
	}
}
