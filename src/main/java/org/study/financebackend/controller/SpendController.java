package org.study.financebackend.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.financebackend.model.FinancialModel;
import org.study.financebackend.model.UserModel;
import org.study.financebackend.model.TransactionType;
import org.study.financebackend.repository.FinancialRepository;
import org.study.financebackend.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

@RestController
@AllArgsConstructor
//@CrossOrigin(allowCredentials = "true",originPatterns = "*")
@Slf4j
public class SpendController {
	private final UserRepository userRepository;
	private final FinancialRepository financialRepository;

	@GetMapping("/get-spend-table")
	public ResponseEntity<UserModel> getAllSpendsByUser(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user) {
		String givenName = user.getAttributes().get("given_name").toString();
		String picture = user.getAttributes().get("picture").toString();
		String email = user.getAttributes().get("email").toString();


		boolean findUserByEmail = userRepository.findUserModelByEmail(email).isPresent();

		if (!findUserByEmail) {
			UserModel newUser = UserModel.builder()
					.email(email)
					.givenName(givenName)
					.picture(picture)
					.allAmount(new LinkedList<>())
					.build();
			userRepository.save(newUser);
			return ResponseEntity.ok(newUser);
		}
		return ResponseEntity.ok(userRepository.findUserModelByEmail(email).get());
	}


	@PostMapping("/{id}")
	public ResponseEntity<String> deleteTransaction(@PathVariable UUID id) {
		log.info("id for delete {}", id);
		financialRepository.deleteById(id);
		return ResponseEntity.ok("Transaction " + id + " was deleted");
	}

	@PostMapping("/update")
	public ResponseEntity<FinancialModel> updateTransaction(
			@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user,
			@RequestBody FinancialModel requestUpdateTransaction
	) {
		System.out.println("user update = " + user);
		System.out.println("requestUpdateTransaction update = " + requestUpdateTransaction);
		UserModel userModel = userRepository.findUserModelByEmail(user.getAttribute("email")).orElseThrow();
		requestUpdateTransaction.setUserModel(userModel);
		financialRepository.save(requestUpdateTransaction);
		return ResponseEntity.ok(financialRepository.save(requestUpdateTransaction));
	}

	@PostMapping("/create")
	public ResponseEntity<?> createTransaction(
			@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user,
			@RequestBody FinancialModel requestUpdateTransaction
	) {

		System.out.println("\"create\" = " + "create");
		UserModel userModel = userRepository.findUserModelByEmail(user.getAttribute("email")).orElseThrow();
		requestUpdateTransaction.setUserModel(userModel);


		if (requestUpdateTransaction.getTransactionType().equals(TransactionType.REGULAR_INCOME)) {
			int month = requestUpdateTransaction.getLocalDate().getMonthValue();
			int day = requestUpdateTransaction.getLocalDate().getDayOfMonth();
			int year = requestUpdateTransaction.getLocalDate().getYear();
			LinkedList<FinancialModel> financialModels = new LinkedList<>();
			while (month <= 12) {
				FinancialModel financialModel = new FinancialModel();
				financialModel.setTitle(requestUpdateTransaction.getTitle());
				financialModel.setAmount(requestUpdateTransaction.getAmount());
				financialModel.setTransactionType(TransactionType.INCOME_AMOUNT);
				financialModel.setUserModel(userModel);
				financialModel.setTransactionId(requestUpdateTransaction.getTransactionId());
				financialModel.setLocalDate(LocalDate.of(year, month, day));
				financialModels.add(financialModel);
				month++;
			}
			log.info("Regular income {}", requestUpdateTransaction);
//			financialRepository.saveAll(financialModels);
			return ResponseEntity.ok(financialRepository.saveAll(financialModels));
		}


		if (requestUpdateTransaction.getTransactionType().equals(TransactionType.REGULAR_SPEND)) {

			int month = requestUpdateTransaction.getLocalDate().getMonthValue();
			int day = requestUpdateTransaction.getLocalDate().getDayOfMonth();
			int year = requestUpdateTransaction.getLocalDate().getYear();
			LinkedList<FinancialModel> financialModels = new LinkedList<>();
			while (month <= 12) {
				FinancialModel financialModel = new FinancialModel();
				financialModel.setTitle(requestUpdateTransaction.getTitle());
				financialModel.setAmount(requestUpdateTransaction.getAmount());
				financialModel.setTransactionType(requestUpdateTransaction.getTransactionType());
				financialModel.setUserModel(userModel);
				financialModel.setTransactionId(requestUpdateTransaction.getTransactionId());
				financialModel.setLocalDate(LocalDate.of(year, month, day));
				financialModels.add(financialModel);
				month++;
			}
//			financialRepository.saveAll(financialModels);
			return ResponseEntity.ok(financialRepository.saveAll(financialModels));
		}

		financialRepository.save(requestUpdateTransaction);
		return ResponseEntity.ok(financialRepository.save(requestUpdateTransaction));
	}

	@GetMapping("/user/logout")
	public void logout(HttpServletResponse response) {
		Cookie refreshCode = annulateCookies("refreshCode");
		Cookie code = annulateCookies("code");
		System.out.println("refreshCode.getValue() = " + refreshCode.getValue());
		System.out.println("code.getValue() = " + code.getValue());
		response.addCookie(refreshCode);
		response.addCookie(code);
	}

	private Cookie annulateCookies(String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setPath("/"); // Задайте шлях cookie. Важливо, щоб він відповідав шляху оригінального cookie
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0); // Встановіть максимальний вік cookie як 0, щоб видалити його
		cookie.setSecure(true); // Встановіть secure, якщо оригінальне cookie було secure
		return cookie;
	}

}
