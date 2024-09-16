package com.fjb.sunrise.dtos.base;

import com.fjb.sunrise.dtos.responses.CategoryFullPageResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryWithIsAdmin extends CategoryFullPageResponse {
    private boolean isAdmin;
}
