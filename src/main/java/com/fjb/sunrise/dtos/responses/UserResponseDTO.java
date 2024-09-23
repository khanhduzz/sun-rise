package com.fjb.sunrise.dtos.responses;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String password; // Nếu không cần thiết, có thể loại bỏ trường này
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String avatarUrl; // Trường lưu URL của avatar
    private MultipartFile avatar; // Trường lưu tệp avatar
}
