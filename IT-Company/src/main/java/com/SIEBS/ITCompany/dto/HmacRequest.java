package com.SIEBS.ITCompany.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HmacRequest {
    private String code;
    private String link;
    private String phoneNumber;
}
