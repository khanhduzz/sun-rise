package com.fjb.sunrise.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileByAdminDTO {
    private Long id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String role;
    private String status;
}
