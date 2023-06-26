package com.SIEBS.ITCompany.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SearchDTO {
    private String email = "";
    private String name = "";
    private String surname = "";
    private String monthNum = "";
}
