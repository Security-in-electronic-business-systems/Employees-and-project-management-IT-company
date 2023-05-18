package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.Role;
import com.SIEBS.ITCompany.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
