package org.study.financebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.study.financebackend.model.FinancialModel;

import java.util.UUID;

@Repository
public interface FinancialRepository extends JpaRepository<FinancialModel, UUID> {
}
