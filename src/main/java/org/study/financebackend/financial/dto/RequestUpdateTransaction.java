package org.study.financebackend.financial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.study.financebackend.financial.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateTransaction {
	private UUID transactionId;
	private String title;
	private BigDecimal amount;
	private TransactionType transactionType;
	private LocalDate localDate;
}
