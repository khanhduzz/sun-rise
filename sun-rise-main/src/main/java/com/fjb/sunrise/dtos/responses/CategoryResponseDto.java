package com.fjb.sunrise.dtos.responses;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryResponseDto {
    private Long id;

    private String name;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
