package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequestResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private boolean isApproved;
    private String title;
    private Role role;
}
