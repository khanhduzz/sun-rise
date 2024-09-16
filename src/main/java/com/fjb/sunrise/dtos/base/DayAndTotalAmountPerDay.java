package com.fjb.sunrise.dtos.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DayAndTotalAmountPerDay implements Serializable {
    private LocalDateTime day;
    private Double amountPerDay;

    public DayAndTotalAmountPerDay() {
    }

    public DayAndTotalAmountPerDay(LocalDateTime day, Double amountPerDay) {
        this.day = day;
        this.amountPerDay = amountPerDay;
    }
}
