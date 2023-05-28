package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.Permission;
import com.SIEBS.ITCompany.model.Project;
import com.SIEBS.ITCompany.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findAll();
}
