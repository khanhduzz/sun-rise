package com.fjb.sunrise.dtos.responses;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StatisticResponse implements Serializable {
    private String totalThisYear;
    private String totalInputThisYear;
    private String totalThisMonth;
}
