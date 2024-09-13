package com.fjb.sunrise.dtos.base;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryAndTotalAmountPerCategory implements Serializable {
    private String category;
    private Double totalAmountPerCategory;

    public CategoryAndTotalAmountPerCategory() {
    }

    public CategoryAndTotalAmountPerCategory(String category, Double totalAmountPerCategory) {
        this.category = category;
        this.totalAmountPerCategory = totalAmountPerCategory;
    }
}
