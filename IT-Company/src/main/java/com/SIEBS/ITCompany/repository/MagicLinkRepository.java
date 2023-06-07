package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.MagicLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MagicLinkRepository extends JpaRepository<MagicLink, Integer> {
    MagicLink findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE MagicLink m SET m.used = true WHERE m.token = :token")
    void setUsedByToken(@Param("token") String token);
}
