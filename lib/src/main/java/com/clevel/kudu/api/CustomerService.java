package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/customer")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Customer Resource", description = "everything for customer")
public interface CustomerService {

    @POST
    @Path("/new")
    @Operation(summary = "create new customer", description = "create new customer")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newCustomer(@Parameter(description = "request: CustomerDTO, result: CustomerDTO", required = true)
                             ServiceRequest<CustomerDTO> request);

    @POST
    @Path("/info")
    @Operation(summary = "get customer information", description = "get customer information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getCustomerInfo(@Parameter(description = "request: SimpleDTO, result: CustomerDTO", required = true)
                                 ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/update")
    @Operation(summary = "update customer information", description = "update customer information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateCustomerInfo(@Parameter(description = "request: CustomerDTO, result: CustomerDTO", required = true)
                                    ServiceRequest<CustomerDTO> request);

    @POST
    @Path("/list")
    @Operation(summary = "get list all customer", description = "get list all customer")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getCustomerList(@Parameter(description = "request: SimpleDTO, result: List\\<CustomerDTO\\>", required = true)
                                 ServiceRequest<SimpleDTO> request);
    @POST
    @Path("/list-Approver")
    @Operation(summary = "get list all Approver", description = "get list all Approver")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getApproverList(@Parameter(description = "request:, result: List\\<UserDTO\\>", required = true)
                                     ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete customer", description = "delete customer")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteCustomer(@Parameter(description = "request: CustomerDTO, result: ServiceResponse", required = true)
                                ServiceRequest<CustomerDTO> request);

}
