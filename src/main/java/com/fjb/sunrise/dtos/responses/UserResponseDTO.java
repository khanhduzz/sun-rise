package com.fjb.sunrise.dtos.responses;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String avatarUrl;
    private MultipartFile avatar;
}
