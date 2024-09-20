package com.fjb.sunrise.mappers;

import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest request);

    User toEntityCreateByAdmin(CreateAndEditUserByAdminDTO byAdminDTO);

    UserResponseDTO toUserResponse(User user);
}
