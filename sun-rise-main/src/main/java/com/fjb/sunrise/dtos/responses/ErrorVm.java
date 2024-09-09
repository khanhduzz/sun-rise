package com.fjb.sunrise.dtos.responses;

import java.util.ArrayList;
import java.util.List;

public record ErrorVm(String statusCode, String title, String detail, List<String> fieldErrors) {

    public ErrorVm(String statusCode, String title, String detail) {
        this(statusCode, title, detail, new ArrayList<String>());
    }
}
