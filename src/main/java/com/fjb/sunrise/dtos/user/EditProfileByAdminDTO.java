package com.fjb.sunrise.dtos.user;

import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private ERole role;  // Updated from String to ERole
    private EStatus status;  // Updated from String to EStatus
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private String avatarPath;  // Added to store avatar path
}
