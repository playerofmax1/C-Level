package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.Holiday;
import com.clevel.kudu.dto.working.HolidayDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HolidayMapper {
    HolidayDTO toDTO(Holiday holiday);
    List<HolidayDTO> toDTO(Stream<Holiday> holidayStream);

    Holiday toEntity(HolidayDTO holidayDTO);
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "status", ignore = true)
    Holiday updateFromDTO(HolidayDTO holidayDTO, @MappingTarget Holiday holiday);
}
