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
public class EmployeeProjectDTO {
    private Integer iD;
    private UsersResponse user;
    private String jobDescription;
    private Date startDate;
    private Date endDate;

}

