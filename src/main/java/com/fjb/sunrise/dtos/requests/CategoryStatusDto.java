package com.fjb.sunrise.dtos.requests;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class CategoryStatusDto implements Serializable {
    private boolean active;
}
