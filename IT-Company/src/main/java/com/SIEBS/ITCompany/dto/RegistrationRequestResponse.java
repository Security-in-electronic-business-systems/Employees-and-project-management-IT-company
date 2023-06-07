package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegistrationRequestResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private boolean isApproved;
    private String title;
    private RoleDTO role;
}
