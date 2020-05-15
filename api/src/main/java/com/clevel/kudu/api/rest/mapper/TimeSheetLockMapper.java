package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.TimeSheetLock;
import com.clevel.kudu.dto.working.TimeSheetLockDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProjectMapper.class, TaskMapper.class, UserMapper.class})
public interface TimeSheetLockMapper {
    TimeSheetLockDTO toDTO(TimeSheetLock timeSheetLock);
    List<TimeSheetLockDTO> toDTO(Stream<TimeSheetLock> timeSheetLockStream);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "modifyBy", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    TimeSheetLock toEntity(TimeSheetLockDTO timeSheetLockDTO);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "modifyBy", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    TimeSheetLock updateFromDTO(TimeSheetLockDTO timeSheetLockDTO, @MappingTarget TimeSheetLock timeSheetLock);
}
