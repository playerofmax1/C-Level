package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.PerformanceYearDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/year")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Performance Year Resource", description = "everything for pfYear")
public interface PerformanceYearService {
    @POST
    @Path("/new")
    @Operation(summary = "create new year at last", description = "create new year at last")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response addLastYear(@Parameter(description = "request: PerformanceYearDTO, result: PerformanceYearDTO", required = true)
                                ServiceRequest<PerformanceYearDTO> request);

    @POST
    @Path("/update")
    @Operation(summary = "update performance year", description = "update performance year")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateYear(@Parameter(description = "request: PerformanceYearDTO, result: PerformanceYearDTO", required = true)
                                ServiceRequest<PerformanceYearDTO> request);

    @POST
    @Path("/list")
    @Operation(summary = "get list all year", description = "get list all year")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getYearList(@Parameter(description = "request: SimpleDTO, result: List\\<PerformanceYearDTO\\>", required = true)
                                    ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete last year", description = "delete last year")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteLastYear(@Parameter(description = "request: PerformanceYearDTO, result: ServiceResponse", required = true)
                                   ServiceRequest<PerformanceYearDTO> request);
}
