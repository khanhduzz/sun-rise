package com.fjb.sunrise.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @Size(max = 250)
    @NotBlank
    private String firstname;

    @Size(max = 250)
    @NotBlank
    private String lastname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(max = 12)
    private String phone;

    @NotEmpty
    private String password;

    private String rePassword;
}
