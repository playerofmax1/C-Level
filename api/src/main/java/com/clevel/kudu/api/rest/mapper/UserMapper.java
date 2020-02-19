package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.User;
import com.clevel.kudu.dto.working.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "password",ignore = true)
    UserDTO toDTO(User user);
    List<UserDTO> toDTO(Stream<User> userStream);

    @Mapping(target = "status",ignore = true)
    @Mapping(target = "rate",ignore = true)
    @Mapping(target = "role",ignore = true)
    @Mapping(target = "lastLoginDate",ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "password",ignore = true)
    @Mapping(target = "role",ignore = true)
    @Mapping(target = "rate",ignore = true)
    @Mapping(target = "status",ignore = true)
    @Mapping(target = "lastLoginDate",ignore = true)
    User updateFromDTO(UserDTO userDTO, @MappingTarget User user);
}
