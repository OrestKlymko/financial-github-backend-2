package org.study.financebackend.financial.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.study.financebackend.auth.model.UserModel;
import org.study.financebackend.auth.repository.UserRepository;
import org.study.financebackend.financial.model.FinancialModel;
import org.study.financebackend.financial.model.TransactionType;
import org.study.financebackend.financial.repository.FinancialRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class FinancialService {

	private final UserRepository userRepository;
	private final FinancialRepository financialRepository;


	public UserModel getAllSpendByUser(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal user) {
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
			return newUser;
		}
		log.info("get table for user {} was success", givenName);
		return userRepository.findUserModelByEmail(email).get();
	}

	public void deleteSpend(UUID id) {
		financialRepository.deleteById(id);
		log.info("Transaction deleted");
	}

	public FinancialModel updateTransaction(OAuth2AuthenticatedPrincipal user,
	                                        FinancialModel requestUpdateTransaction) {
		UserModel userModel = userRepository.findUserModelByEmail(user.getAttribute("email")).orElseThrow();
		requestUpdateTransaction.setUserModel(userModel);
		financialRepository.save(requestUpdateTransaction);
		log.info("Transaction {} updated", requestUpdateTransaction);
		return financialRepository.save(requestUpdateTransaction);
	}

	public List<FinancialModel> createTransaction(OAuth2AuthenticatedPrincipal user,
	                                              FinancialModel requestUpdateTransaction){
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
			log.info("Regular income transaction {}", requestUpdateTransaction);
			return financialRepository.saveAll(financialModels);
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
			log.info("Regular spend transaction {}", requestUpdateTransaction);
			return financialRepository.saveAll(financialModels);
		}
		log.info("One transaction to create {}", requestUpdateTransaction);
		return Collections.singletonList(financialRepository.save(requestUpdateTransaction));
	}
}
