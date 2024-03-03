package org.study.financebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
