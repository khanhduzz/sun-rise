package com.fjb.sunrise.dtos.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CategoryStatusDto implements Serializable {
    private boolean active;
}
