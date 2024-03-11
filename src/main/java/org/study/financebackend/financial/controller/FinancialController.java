package org.study.financebackend.financial.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.financebackend.financial.model.FinancialModel;
import org.study.financebackend.auth.model.UserModel;
import org.study.financebackend.financial.service.FinancialService;

import java.util.*;

@RestController
@AllArgsConstructor
@Slf4j
public class FinancialController {
	private final FinancialService financialService;

	@GetMapping("/get-spend-table")
	public ResponseEntity<UserModel> getAllSpendsByUser(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user) {
		return ResponseEntity.ok(financialService.getAllSpendByUser(user));
	}


	@PostMapping("/{id}")
	public ResponseEntity<String> deleteTransaction(@PathVariable UUID id) {
		financialService.deleteSpend(id);
		return ResponseEntity.ok("Transaction " + id + " was deleted");
	}

	@PostMapping("/update")
	public ResponseEntity<FinancialModel> updateTransaction(
			@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user,
			@RequestBody FinancialModel requestUpdateTransaction
	) {
		return ResponseEntity.ok(financialService.updateTransaction(user, requestUpdateTransaction));
	}

	@PostMapping("/create")
	public ResponseEntity<?> createTransaction(
			@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user,
			@RequestBody FinancialModel requestUpdateTransaction
	) {
		return ResponseEntity.ok(financialService.createTransaction(user, requestUpdateTransaction));
	}

}
