package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.File;
import com.SIEBS.ITCompany.model.Skill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

}
