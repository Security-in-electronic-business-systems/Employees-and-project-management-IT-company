package com.SIEBS.ITCompany.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Integer id;
    Date date;
    String message;
    Boolean opened;

    public Notification(Date date, String message, Boolean opened) {
        this.date = date;
        this.message = message;
        this.opened = opened;
    }
}
