package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.Task;
import com.clevel.kudu.dto.working.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDTO toDTO(Task task);
    List<TaskDTO> toDTO(Stream<Task> taskStream);

    Task toEntity(TaskDTO taskDTO);
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "status", ignore = true)
    Task updateFromDTO(TaskDTO taskDTO, @MappingTarget Task task);
}
