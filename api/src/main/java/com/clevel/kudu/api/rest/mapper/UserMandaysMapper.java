package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.view.UserMandays;
import com.clevel.kudu.dto.working.UserMandaysDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMandaysMapper {
    @Mapping(target = "userId", source = "user.id")
    UserMandaysDTO toDTO(UserMandays userMandays);

    List<UserMandaysDTO> toDTO(Stream<UserMandays> userMandaysStream);
}
