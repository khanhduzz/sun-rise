package com.fjb.sunrise.dtos.base;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryAndTotalAmountPerCategoryForChart implements Serializable {
    private String category;
    private String totalAmountPerCategory;

    public CategoryAndTotalAmountPerCategoryForChart() {
    }

    public CategoryAndTotalAmountPerCategoryForChart(String category, String totalAmountPerCategory) {
        this.category = category;
        this.totalAmountPerCategory = totalAmountPerCategory;
    }
}
