package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.Project;
import com.clevel.kudu.dto.working.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDTO toDTO(Project project);
    List<ProjectDTO> toDTO(Stream<Project> projectStream);

    @Mapping(target = "customer",ignore = true)
    Project toEntity(ProjectDTO projectDTO);
    @Mapping(target = "customer",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "status", ignore = true)
    Project updateFromDTO(ProjectDTO projectDTO, @MappingTarget Project project);
}
