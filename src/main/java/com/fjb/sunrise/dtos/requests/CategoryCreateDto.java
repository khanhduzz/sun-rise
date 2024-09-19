package com.fjb.sunrise.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryCreateDto implements Serializable {
    @NotBlank(message = "Vui lòng nhâp tên danh mục")
    @Size(max = 50, message = "Không vượt quá 50 ký tự")
    private String name;
}
