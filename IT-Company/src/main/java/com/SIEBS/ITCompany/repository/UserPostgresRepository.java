package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPostgresRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
