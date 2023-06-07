package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.Role;
import com.SIEBS.ITCompany.model.Address;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String title;
    private Address address;
    private RoleDTO role;
    private String message;
}
