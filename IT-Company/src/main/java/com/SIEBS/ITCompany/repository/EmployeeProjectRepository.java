package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.EmployeeProject;
import com.SIEBS.ITCompany.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Integer> {

}
