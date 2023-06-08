package com.SIEBS.ITCompany.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectsDTO {
    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;
    private String description;

}
