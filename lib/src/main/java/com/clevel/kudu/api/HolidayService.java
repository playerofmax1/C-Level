package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.HolidayDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/holiday")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Holiday Resource", description = "everything for holiday")
public interface HolidayService {
    @POST
    @Path("/new")
    @Operation(summary = "create new holiday", description = "create new holiday")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newHoliday(@Parameter(description = "request: HolidayDTO, result: HolidayDTO", required = true)
                                 ServiceRequest<HolidayDTO> request);

    @POST
    @Path("/info")
    @Operation(summary = "get holiday information", description = "get holiday information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getHolidayInfo(@Parameter(description = "request: SimpleDTO, result: HolidayDTO", required = true)
                                     ServiceRequest<HolidayDTO> request);

    @POST
    @Path("/update")
    @Operation(summary = "update holiday information", description = "update holiday information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateHolidayInfo(@Parameter(description = "request: HolidayDTO, result: HolidayDTO", required = true)
                                        ServiceRequest<HolidayDTO> request);

    @POST
    @Path("/list")
    @Operation(summary = "get list all holiday", description = "get list all holiday")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getHolidayList(@Parameter(description = "request: SimpleDTO, result: List\\<HolidayDTO\\>", required = true)
                                     ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete holiday", description = "delete holiday")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteHoliday(@Parameter(description = "request: HolidayDTO, result: ServiceResponse", required = true)
                                    ServiceRequest<HolidayDTO> request);

}
