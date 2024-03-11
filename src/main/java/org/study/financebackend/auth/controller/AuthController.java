package org.study.financebackend.auth.controller;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.study.financebackend.auth.dto.UrlDto;
import org.study.financebackend.auth.service.AuthenticationService;

import java.io.IOException;

@RestController
@Slf4j
public class AuthController {

	@Autowired
	private AuthenticationService authenticationService;

	@GetMapping("/auth/url")
	public ResponseEntity<UrlDto> auth() {
		String urlForLogin = authenticationService.getUrlForLogin();
		return ResponseEntity.ok(new UrlDto(urlForLogin));
	}

	@GetMapping("/auth/callback")
	public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletResponse response) {
		try {
			authenticationService.callback(code, response);
			return ResponseEntity.ok().build();
		} catch (IOException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/user/logout")
	public void logout(HttpServletResponse response) {
		authenticationService.logout(response);
	}

}
