package org.study.financebackend.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;


@Entity
@Slf4j
@Table(name = "month_amount")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID userId;
	@Column(name = "given_name")
	private String givenName;
	private String picture;
	private String email;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "userModel")
	@JsonManagedReference
	private List<FinancialModel> allAmount;
}
