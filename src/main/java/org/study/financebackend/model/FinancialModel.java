package org.study.financebackend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "financial_model")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialModel {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "transaction_id")
	private UUID transactionId;
	@Column(name = "title")
	private String title;
	@Column(name = "amount")
	private BigDecimal amount;
	@Column(name = "transaction_type")
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonBackReference
	@ToString.Exclude
	private UserModel userModel;
	@Column(name = "locale_date")
	private LocalDate localDate;

}
