package com.fjb.sunrise.dtos.requests;

import com.fjb.sunrise.enums.EStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryStatusDto {
    private Long id;
    private EStatus status;
}
