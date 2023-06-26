package com.SIEBS.ITCompany.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    private String email;
    private String newPassword;
    private String oldPassword;
}
