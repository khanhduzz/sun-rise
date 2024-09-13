package com.fjb.sunrise.dtos.responses;

import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import lombok.Data;


@Data
public class CategoryFullPageResponse implements Serializable {
    @Transient
    private List<CategoryResponseDto> data;
    private Long recordsTotal;
    private Long recordsFiltered;
    private Integer draw;
}
