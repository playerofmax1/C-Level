package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.ProjectTask;
import com.clevel.kudu.dto.working.ProjectTaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE,
uses = {ProjectMapper.class, TaskMapper.class, UserMapper.class})
public interface ProjectTaskMapper {
    @Mapping(target = "totalMD", expression = "java(com.clevel.kudu.util.DateTimeUtil.getTotalMD(projectTask.getPlanMDMinute(),projectTask.getExtendMDMinute()))")
    @Mapping(target = "totalMDDuration", expression = "java(com.clevel.kudu.util.DateTimeUtil.getTotalDuration(projectTask.getPlanMDMinute(),projectTask.getExtendMDMinute()))")
    @Mapping(target = "totalMDMinute", expression = "java(com.clevel.kudu.util.DateTimeUtil.getTotalMinute(projectTask.getPlanMDMinute(),projectTask.getExtendMDMinute()))")
    @Mapping(target = "user.rate",ignore = true)
    @Mapping(target = "user.role",ignore = true)
    ProjectTaskDTO toDTO(ProjectTask projectTask);
    List<ProjectTaskDTO> toDTO(Stream<ProjectTask> projectTaskStream);

    @Mapping(target = "project",ignore = true)
    @Mapping(target = "task",ignore = true)
    @Mapping(target = "user",ignore = true)
    ProjectTask toEntity(ProjectTaskDTO projectTaskDTO);
    @Mapping(target = "project",ignore = true)
    @Mapping(target = "task",ignore = true)
    @Mapping(target = "user",ignore = true)
    ProjectTask updateFromDTO(ProjectTaskDTO projectTaskDTO, @MappingTarget ProjectTask projectTask);
}
