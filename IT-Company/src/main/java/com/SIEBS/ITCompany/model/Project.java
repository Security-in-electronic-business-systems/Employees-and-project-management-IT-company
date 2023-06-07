package com.SIEBS.ITCompany.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Date startDate;

    private Date endDate;

    private String description;

    @OneToMany(mappedBy = "project")
    private List<EmployeeProject> employeeProjects;
}
