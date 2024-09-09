package com.fjb.sunrise.dtos.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
public class CategorySearchDto {
    private String searchQuery; // Add fields for search criteria as needed
}
