package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.ProjectTaskExt;
import com.clevel.kudu.dto.working.ProjectTaskExtDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectTaskExtMapper {
    ProjectTaskExtDTO toDTO(ProjectTaskExt projectTaskExt);
    List<ProjectTaskExtDTO> toDTO(Stream<ProjectTaskExt> projectTaskExtStream);

    ProjectTaskExt toEntity(ProjectTaskExtDTO projectTaskExtDTO);
    ProjectTaskExt updateFromDTO(ProjectTaskExtDTO projectTaskExtDTO, @MappingTarget ProjectTaskExt projectTaskExt);
}
