package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;


    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        return users;
    }
}
