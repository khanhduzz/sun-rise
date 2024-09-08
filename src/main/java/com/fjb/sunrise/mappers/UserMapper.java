package com.fjb.sunrise.mappers;

import com.fjb.sunrise.dtos.EditProfileByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest request);

    User toEntityByAdmin(EditProfileByAdminDTO byAdminDTO);

    EditProfileByAdminDTO toUserByAdmin(User user);
}
