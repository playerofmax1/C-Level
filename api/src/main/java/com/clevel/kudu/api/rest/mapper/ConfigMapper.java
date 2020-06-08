package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.system.Config;
import com.clevel.kudu.dto.working.ConfigDTO;
import com.clevel.kudu.model.SystemConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {SystemConfig.class})
public interface ConfigMapper {
    @Mapping(target = "systemConfig", expression = "java( SystemConfig.parse(config.getName()) )")
    ConfigDTO toDTO(Config config);
    List<ConfigDTO> toDTO(Stream<Config> configStream);

    Config toEntity(ConfigDTO configDTO);
    Config updateFromDTO(ConfigDTO configDTO, @MappingTarget Config config);
}
