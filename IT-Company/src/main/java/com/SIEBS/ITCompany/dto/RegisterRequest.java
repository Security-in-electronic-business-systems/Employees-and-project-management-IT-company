package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.enumerations.Role;
import com.SIEBS.ITCompany.model.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phoneNumber;
    private String title;
    private Address address;
    private Role role;
}
