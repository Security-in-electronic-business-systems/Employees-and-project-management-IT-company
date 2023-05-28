package com.SIEBS.ITCompany.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Permission {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private Methods permision;

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", role=" + role +
                ", permision=" + permision +
                '}';
    }
}
