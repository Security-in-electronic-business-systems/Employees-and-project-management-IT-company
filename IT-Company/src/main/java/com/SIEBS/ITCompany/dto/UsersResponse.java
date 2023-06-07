package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.Address;
import com.SIEBS.ITCompany.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String title;
    private Address address;
    private RoleDTO role;
}
