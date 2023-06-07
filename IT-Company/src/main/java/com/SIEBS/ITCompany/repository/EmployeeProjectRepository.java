package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.EmployeeProject;
import com.SIEBS.ITCompany.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Integer> {
}
