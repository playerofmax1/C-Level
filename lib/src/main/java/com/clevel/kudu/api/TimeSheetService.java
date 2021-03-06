package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.rpt.MandaysReportRequest;
import com.clevel.kudu.dto.working.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/timeSheet")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "TimeSheet Resource", description = "everything for TimeSheet")
public interface TimeSheetService {
    @POST
    @Path("/list")
    @Operation(summary = "get timeSheet for month n", description = "get timeSheet for month n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getTimeSheet(@Parameter(description = "request: TimeSheetRequest, result: TimeSheetResult", required = true)
                                    ServiceRequest<TimeSheetRequest> request);
    @POST
    @Path("/info")
    @Operation(summary = "get timeSheet for month n", description = "get timeSheet for month n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getTimeSheetInfo(@Parameter(description = "request: TimeSheetDTO, result: List\\<TimeSheetDTO\\>", required = true)
                                  ServiceRequest<TimeSheetDTO> request);

    @POST
    @Path("/mandays")
    @Operation(summary = "get mandays for a user at year n", description = "get mandays for a user at year n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getMandays(@Parameter(description = "request: MandaysRequest, result: MandaysResult", required = true)
                                  ServiceRequest<MandaysRequest> request);

    @POST
    @Path("/mandaysreport")
    @Operation(summary = "get mandays report for a specified year", description = "get mandays report for a specified year")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getMandaysReport(@Parameter(description = "request: MandaysReportRequest, result: MandaysReportResult", required = true)
                                  ServiceRequest<MandaysReportRequest> request);

    @POST
    @Path("/new")
    @Operation(summary = "create new timeSheet record", description = "create new timeSheet record")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newRecord(@Parameter(description = "request: TimeSheetDTO, result: TimeSheetDTO", required = true)
                             ServiceRequest<TimeSheetDTO> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete timeSheet record", description = "delete timeSheet record")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteRecord(@Parameter(description = "request: TimeSheetDTO, result: ServiceResponse", required = true)
                                ServiceRequest<TimeSheetDTO> request);

    @POST
    @Path("/save")
    @Operation(summary = "save timeSheet record", description = "save timeSheet record")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response saveRecord(@Parameter(description = "request: TimeSheetDTO, result: TimeSheetDTO", required = true)
                               ServiceRequest<TimeSheetDTO> request);

    @POST
    @Path("/project/list")
    @Operation(summary = "get list all project based on user", description = "get list all project based on user")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectList(@Parameter(description = "request: SimpleDTO(userId), result: List\\<ProjectDTO\\>", required = true)
                                        ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/task/list")
    @Operation(summary = "get list all task based on project", description = "get list all task based on project")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getTaskList(@Parameter(description = "request: ProjectTaskRequest, result: List\\<TaskDTO\\>", required = true)
                                        ServiceRequest<ProjectTaskRequest> request);

    @POST
    @Path("/utilization")
    @Operation(summary = "get utilization for month n", description = "get utilization for month n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getUtilization(@Parameter(description = "request: UtilizationRequest, result: UtilizationResult", required = true)
                                      ServiceRequest<UtilizationRequest> request);

    @POST
    @Path("/target/utilization")
    @Operation(summary = "save target utilization for year n", description = "save target utilization for year n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response saveTargetUtilization(@Parameter(description = "request: TargetUtilizationRequest, result: TargetUtilizationResult", required = true)
                                      ServiceRequest<TargetUtilizationRequest> request);

    @POST
    @Path("/reset")
    @Operation(summary = "reset timeSheet record", description = "reset timeSheet record")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response resetRecord(@Parameter(description = "request: TimeSheetDTO, result: TimeSheetDTO", required = true)
                               ServiceRequest<TimeSheetDTO> request);

    @POST
    @Path("/special/migrate/workhour")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response migrateWorkHour(ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/lock")
    @Operation(summary = "lock timeSheet for month n", description = "lock timeSheet for month n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response lockTimeSheet(@Parameter(description = "request: TimeSheetRequest, result: TimeSheetResult", required = true)
                                  ServiceRequest<TimeSheetRequest> request);

    @POST
    @Path("/unlock")
    @Operation(summary = "unlock timeSheet for month n", description = "unlock timeSheet for month n")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response unlockTimeSheet(@Parameter(description = "request: TimeSheetRequest, result: TimeSheetResult", required = true)
                                  ServiceRequest<TimeSheetRequest> request);

}
