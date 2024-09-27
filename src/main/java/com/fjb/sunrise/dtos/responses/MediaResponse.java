package com.fjb.sunrise.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MediaResponse {
    private String name;
    private String fileCode;
    private String url;
    private String type;
    private long size;
}
