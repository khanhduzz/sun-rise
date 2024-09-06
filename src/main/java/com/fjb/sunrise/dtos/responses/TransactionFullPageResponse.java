package com.fjb.sunrise.dtos.responses;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class TransactionFullPageResponse implements Serializable {
    private List<TransactionPageResponse> data;
    private Long recordsTotal;
    private Long recordsFiltered;
    private Integer draw;
}
