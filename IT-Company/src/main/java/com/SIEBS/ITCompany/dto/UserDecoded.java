package com.SIEBS.ITCompany.dto;
import com.SIEBS.ITCompany.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDecoded {
    private String title;
    private Address adress;
}