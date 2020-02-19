package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.master.Rate;
import com.clevel.kudu.dto.working.RateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RateMapper {
    RateDTO toDTO(Rate rate);
    List<RateDTO> toDTO(Stream<Rate> rateStream);

    Rate toEntity(RateDTO rateDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Rate updateFromDTO(RateDTO rateDTO, @MappingTarget Rate rate);

}
