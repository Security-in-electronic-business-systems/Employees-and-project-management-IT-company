package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.model.Permission;
import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.model.UserRole;
import com.SIEBS.ITCompany.repository.PermissionRepository;
import com.SIEBS.ITCompany.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    private final PermissionRepository repository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public boolean hasPermission(String permission){
        User loggedUser = authenticationService.getLoggedUser();
        List<Permission> permissions= repository.findAll();
        for (Permission per: permissions) {
            for (UserRole role: loggedUser.getRoles()) {
               if (per.getRole().getName().equals(role.getRole().getName())){
                   if (per.getPermision().toString().equals(permission)){
                       return true;
                   }
               }
            }
        }
        log.error("Permission denied. User: " + authenticationService.removeDangerousCharacters(loggedUser.getEmail()) + "permission: " + authenticationService.removeDangerousCharacters(permission) );
        return false;
    }

}
