package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.MagicLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagicLinkRepository extends JpaRepository<MagicLink, Integer> {
    MagicLink findByToken(String token);
}
