package com.fjb.sunrise.dtos.responses;

import java.io.Serializable;
import java.util.List;
import lombok.Data;


@Data
public class CategoryFullPageResponse implements Serializable {
    private transient List<CategoryResponseDto> data;
    private Long recordsTotal;
    private Long recordsFiltered;
    private Integer draw;
}
