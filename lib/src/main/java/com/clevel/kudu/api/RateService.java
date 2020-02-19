package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.RateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/rate")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Rate Resource", description = "everything for rate")
public interface RateService {
    @POST
    @Path("/new")
    @Operation(summary = "create new rate", description = "create new rate")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newRate(@Parameter(description = "request: RateDTO, result: RateDTO", required = true)
                                 ServiceRequest<RateDTO> request);

    @POST
    @Path("/info")
    @Operation(summary = "get rate information", description = "get rate information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getRateInfo(@Parameter(description = "request: SimpleDTO, result: RateDTO", required = true)
                                     ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/update")
    @Operation(summary = "update rate information", description = "update rate information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateRateInfo(@Parameter(description = "request: RateDTO, result: RateDTO", required = true)
                                        ServiceRequest<RateDTO> request);

    @POST
    @Path("/list")
    @Operation(summary = "get list all rate", description = "get list all rate")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getRateList(@Parameter(description = "request: SimpleDTO, result: List\\<RateDTO\\>", required = true)
                                     ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete rate", description = "delete rate")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteRate(@Parameter(description = "request: RateDTO, result: ServiceResponse", required = true)
                                    ServiceRequest<RateDTO> request);

}
