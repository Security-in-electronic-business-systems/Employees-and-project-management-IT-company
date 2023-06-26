package com.SIEBS.ITCompany.model;


import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Logs {
    private Date date;
    private String type;
    private String component;
    private String Message;
}
