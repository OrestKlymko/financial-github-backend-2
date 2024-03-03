package org.study.financebackend.dtos;


import org.study.financebackend.model.TransactionType;

import java.math.BigDecimal;


public record MonthAdd(
		String title,
		BigDecimal amount,
		TransactionType transactionType
) {
}
