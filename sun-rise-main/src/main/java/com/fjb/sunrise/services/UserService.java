package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.RegisterRequest;

public interface UserService {
    boolean checkRegister(RegisterRequest registerRequest);
}
