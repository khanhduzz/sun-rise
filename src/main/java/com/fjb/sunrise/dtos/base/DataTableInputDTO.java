package com.fjb.sunrise.dtos.base;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataTableInputDTO {
    private Integer draw;
    private List<Map<String, Object>> columns;
    private List<Map<String, String>> order;
    private Integer start;
    private Integer length;
    private Map<String, String> search;

    public DataTableInputDTO() {

    }
}