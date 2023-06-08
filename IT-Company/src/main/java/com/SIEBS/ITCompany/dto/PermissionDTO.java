package com.SIEBS.ITCompany.dto;

import com.SIEBS.ITCompany.model.Methods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Integer id;
    private String role;
    private List<Methods> methods;

    public void addMethod(Methods m) {
        methods.add(m);
    }
}
