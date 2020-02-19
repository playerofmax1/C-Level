package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.TimeSheet;
import com.clevel.kudu.dto.working.TimeSheetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProjectMapper.class, TaskMapper.class, UserMapper.class})
public interface TimeSheetMapper {
    TimeSheetDTO toDTO(TimeSheet timeSheet);
    List<TimeSheetDTO> toDTO(Stream<TimeSheet> timeSheetStream);

    TimeSheet toEntity(TimeSheetDTO timeSheetDTO);
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "projectTask", ignore = true)
    @Mapping(target = "task", ignore = true)
    TimeSheet updateFromDTO(TimeSheetDTO timeSheetDTO, @MappingTarget TimeSheet timeSheet);
}
