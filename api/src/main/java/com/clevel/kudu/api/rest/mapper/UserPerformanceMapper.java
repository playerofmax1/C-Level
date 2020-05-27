package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.UserPerformance;
import com.clevel.kudu.dto.working.UserPerformanceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE,
uses = {PerformanceYearMapper.class})
public interface UserPerformanceMapper {

    UserPerformanceDTO toDTO(UserPerformance userPerformance);

}
