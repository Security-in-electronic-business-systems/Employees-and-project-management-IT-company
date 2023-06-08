package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.EmployeeProject;
import com.SIEBS.ITCompany.model.User;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditEmployeeDTO {
    private Integer id;
    private Integer userId;
    private Date startDate;
    private Date endDate;
    private String jobDescription;
//    private List<UsersResponse> usersResponses;
}
