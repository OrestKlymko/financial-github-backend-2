package org.study.financebackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.study.financebackend.auth.model.UserModel;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
	Optional<UserModel> findUserModelByEmail(String email);
}
