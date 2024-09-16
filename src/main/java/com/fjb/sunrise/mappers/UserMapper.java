package com.fjb.sunrise.mappers;

import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "avatarImage", source = "avatarImage", qualifiedByName = "byteArrayToBase64")
    UserResponseDTO toUserResponse(User user);

    @Named("byteArrayToBase64")
    default String byteArrayToBase64(byte[] avatarImage) {
        if (avatarImage == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(avatarImage);
    }

    User toEntity(RegisterRequest registerRequest);

    User toEntityByAdmin(EditProfileByAdminDTO byAdminDTO);
}
