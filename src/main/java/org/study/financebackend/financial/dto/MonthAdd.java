package org.study.financebackend.financial.dto;


import org.study.financebackend.financial.model.TransactionType;

import java.math.BigDecimal;


public record MonthAdd(
		String title,
		BigDecimal amount,
		TransactionType transactionType
) {
}
