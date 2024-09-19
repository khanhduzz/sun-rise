package com.fjb.sunrise.dtos.responses;

import com.fjb.sunrise.dtos.base.DayAndTotalAmountPerDayForChart;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class StatisticResponse implements Serializable {
    private List<DayAndTotalAmountPerDayForChart> totalIn3Month;
    private String totalThisYear;
    private String totalInputThisYear;
    private String totalThisMonth;
    private transient List<?> totalThisMonthByCategory;
}
