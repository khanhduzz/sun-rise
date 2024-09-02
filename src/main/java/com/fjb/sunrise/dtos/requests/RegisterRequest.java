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
    @NotBlank(message = "Tên không được để trống")
    private String firstname;

    @Size(max = 250)
    @NotBlank(message = "Họ không được để trống")
    private String lastname;

    @Email(message = "Email phải có dạng của email")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 10, message = "Số điện thoại có đúng 10 chữ số")
    private String phone;

    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;
}
