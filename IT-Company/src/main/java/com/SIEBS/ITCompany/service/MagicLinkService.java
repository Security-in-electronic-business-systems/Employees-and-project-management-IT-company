package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.model.MagicLink;
import com.SIEBS.ITCompany.repository.MagicLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MagicLinkService {
    private final MagicLinkRepository repository;

    public boolean isTokenUsed(String token){
        MagicLink ml = repository.findByToken(token);
        return ml.isUsed();
    }

    public void Save(MagicLink ml){
        repository.save(ml);
    }

    public void setUsedByToken(String token){
        repository.setUsedByToken(token);
    }
}