package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.dto.security.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    List<RoleDTO> toDTO(Stream<Role> roleStream);

    Role toEntity(RoleDTO roleDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Role updateFromDTO(RoleDTO roleDTO, @MappingTarget Role role);
}
