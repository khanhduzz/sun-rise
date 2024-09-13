package com.fjb.sunrise.dtos.base;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DayAndTotalAmountPerDayForChart implements Serializable {
    private String day;
    private String amountPerDay;

    public DayAndTotalAmountPerDayForChart() {
    }

    public DayAndTotalAmountPerDayForChart(String day, String amountPerDay) {
        this.day = day;
        this.amountPerDay = amountPerDay;
    }
}
