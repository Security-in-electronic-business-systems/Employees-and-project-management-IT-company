package com.SIEBS.ITCompany.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int grade;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
