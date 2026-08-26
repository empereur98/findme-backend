package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.UserResponse;
import com.dhi.findme_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "registrationDate", source = "registrationDate")
    UserResponse toUserResponse(User entity);
}
