package com.clevel.kudu.api.rest.mapper;

import com.clevel.kudu.api.model.db.working.Customer;
import com.clevel.kudu.dto.working.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    List<CustomerDTO> toDTO(Stream<Customer> customerStream);

    Customer toEntity(CustomerDTO customerDTO);
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "status", ignore = true)
    Customer updateFromDTO(CustomerDTO customerDTO, @MappingTarget Customer customer);
}
