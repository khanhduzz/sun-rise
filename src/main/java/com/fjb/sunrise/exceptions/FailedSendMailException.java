package com.fjb.sunrise.exceptions;

import com.fjb.sunrise.utils.MessagesUtils;
import lombok.Setter;

@Setter
public class FailedSendMailException extends RuntimeException {
    private final String message;

    public FailedSendMailException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
