package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.PerformanceYear;
import com.clevel.kudu.dto.working.PerformanceYearDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PerformanceYearMapper {

    PerformanceYearDTO toDTO(PerformanceYear performanceYear);

}
