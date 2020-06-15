package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.MandaysRequest;
import com.clevel.kudu.dto.working.MandaysRequestDTO;
import com.clevel.kudu.model.MandaysRequestType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ProjectTaskMapper.class, ProjectMapper.class, TaskMapper.class, UserMapper.class},
        imports = MandaysRequestType.class)
public interface MandaysRequestMapper {

    @Mapping(target = "requestDate", source = "createDate")
    @Mapping(target = "type", expression = "java( mandaysRequest.getProjectTask()==null?MandaysRequestType.NEW:MandaysRequestType.EXTEND)")
    MandaysRequestDTO toDTO(MandaysRequest mandaysRequest);

    List<MandaysRequestDTO> toDTO(Stream<MandaysRequest> mandaysRequestStream);

    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "modifyBy", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    MandaysRequest toEntity(MandaysRequestDTO mandaysRequestDTO);

    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "modifyBy", ignore = true)
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    MandaysRequest updateFromDTO(MandaysRequestDTO mandaysRequestDTO, @MappingTarget MandaysRequest mandaysRequest);
}
