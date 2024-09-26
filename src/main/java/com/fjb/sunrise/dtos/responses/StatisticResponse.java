package com.fjb.sunrise.dtos.responses;

import java.io.Serializable;
import lombok.Data;



@Data
public class StatisticResponse implements Serializable {
    private String totalThisYear;
    private String totalInputThisYear;
    private String totalThisMonth;
}
