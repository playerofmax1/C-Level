package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.UserTimeSheet;
import com.clevel.kudu.dto.working.UserTimeSheetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserTSMapper {
    @Mapping(target = "user.rate",ignore = true)
    @Mapping(target = "user.role",ignore = true)
    @Mapping(target = "user.password",ignore = true)
    @Mapping(target = "timeSheetUser.rate",ignore = true)
    @Mapping(target = "timeSheetUser.role",ignore = true)
    @Mapping(target = "timeSheetUser.password",ignore = true)
    UserTimeSheetDTO toDTO(UserTimeSheet userTimeSheet);
    List<UserTimeSheetDTO> toDTO(Stream<UserTimeSheet> userTimeSheetStream);
}
