package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDTO {
    private Long id;
    private String name;
    private int grade;
    private String email;
}
