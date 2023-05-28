package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
}
