package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
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

@Path("/project")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Project Resource", description = "everything for Project")
public interface ProjectService {
    @POST
    @Path("/new")
    @Operation(summary = "create new project", description = "create new project")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newProject(@Parameter(description = "request: ProjectDTO, result: ProjectDTO", required = true)
                                 ServiceRequest<ProjectDTO> request);

    @POST
    @Path("/info")
    @Operation(summary = "get project information", description = "get project information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectInfo(@Parameter(description = "request: SimpleDTO, result: ProjectDTO", required = true)
                                     ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/update")
    @Operation(summary = "update project information", description = "update project information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateProjectInfo(@Parameter(description = "request: ProjectDTO, result: ProjectDTO", required = true)
                                        ServiceRequest<ProjectDTO> request);

    @POST
    @Path("/list")
    @Operation(summary = "get list all projects", description = "get list all projects")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectList(@Parameter(description = "request: SimpleDTO, result: List\\<ProjectDTO\\>", required = true)
                                     ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/close")
    @Operation(summary = "close project", description = "close project")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response closeProject(@Parameter(description = "request: ProjectDTO, result: ServiceResponse", required = true)
                                   ServiceRequest<ProjectDTO> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete project", description = "delete project")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteProject(@Parameter(description = "request: ProjectDTO, result: ServiceResponse", required = true)
                                    ServiceRequest<ProjectDTO> request);

    @POST
    @Path("/search")
    @Operation(summary = "search project", description = "search project")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response searchProject(@Parameter(description = "request: SearchRequest, result: List\\<ProjectDTO\\>", required = true)
                                    ServiceRequest<SearchRequest> request);

    // Project task
    @POST
    @Path("/task/list")
    @Operation(summary = "get list all project tasks", description = "get list all project tasks")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectTaskList(@Parameter(description = "request: SimpleDTO, result: List\\<ProjectTaskDTO\\>", required = true)
                                    ServiceRequest<SimpleDTO> request);
    @POST
    @Path("/task/info")
    @Operation(summary = "get project information", description = "get project information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectTaskInfo(@Parameter(description = "request: SimpleDTO, result: ProjectDTO", required = true)
                                    ServiceRequest<SimpleDTO> request);

    // Project task ext
    @POST
    @Path("/task/list/ext")
    @Operation(summary = "get list all project task extend", description = "get list all project task extend")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectTaskExtList(@Parameter(description = "request: SimpleDTO, result: List\\<ProjectTaskExtDTO\\>", required = true)
                                        ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/task/list/user")
    @Operation(summary = "get list all project tasks for user", description = "get list all project tasks for user")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectTaskListForUser(@Parameter(description = "request: SimpleDTO, result: List\\<ProjectTaskDTO\\>", required = true)
                                        ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/task/new")
    @Operation(summary = "create new project task", description = "create new project task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newProjectTask(@Parameter(description = "request: ProjectTaskDTO, result: ProjectTaskDTO", required = true)
                                ServiceRequest<ProjectTaskDTO> request);

    @POST
    @Path("/task/new/ext")
    @Operation(summary = "create ext project task", description = "create ext project task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newExtProjectTask(@Parameter(description = "request: ProjectTaskExtRequest, result: ProjectTaskExtDTO", required = true)
                                    ServiceRequest<ProjectTaskExtRequest> request);

    @POST
    @Path("/task/update")
    @Operation(summary = "update project task information", description = "update project task information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateProjectTaskInfo(@Parameter(description = "request: ProjectTaskDTO, result: ProjectTaskDTO", required = true)
                                       ServiceRequest<ProjectTaskDTO> request);

    @POST
    @Path("/cost")
    @Operation(summary = "get project cost", description = "get project cost")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getProjectCost(@Parameter(description = "request: SimpleDTO, result: ProjectCostDTO", required = true)
                                    ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/task/delete")
    @Operation(summary = "delete project task", description = "delete project task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteProjectTask(@Parameter(description = "request: ProjectTaskDTO, result: ServiceResponse", required = true)
                                   ServiceRequest<ProjectTaskDTO> request);

    @POST
    @Path("/task/lock")
    @Operation(summary = "lock project task", description = "lock project task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response lockProjectTask(@Parameter(description = "request: ProjectTaskDTO, result: ServiceResponse", required = true)
                                   ServiceRequest<ProjectTaskDTO> request);

    @POST
    @Path("/task/unlock")
    @Operation(summary = "unlock project task", description = "unlock project task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response unlockProjectTask(@Parameter(description = "request: ProjectTaskDTO, result: ServiceResponse", required = true)
                                     ServiceRequest<ProjectTaskDTO> request);

    @POST
    @Path("/special/task/recal/amd")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response reCalculatePercentAMD(ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/mdrequest/list")
    @Operation(summary = "list mandays request by user and status", description = "list mandays request by user and status")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getMandaysRequestList(@Parameter(description = "request: UserStatusRequest, result: MandaysRequestResult", required = true)
                                       ServiceRequest<UserStatusRequest> request);

    @POST
    @Path("/mdrequest/new")
    @Operation(summary = "create mandays request", description = "create mandays request")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newMandaysRequest(@Parameter(description = "request: MandaysRequestDTO, result: MandaysRequestDTO", required = true)
                                       ServiceRequest<MandaysRequestDTO> request);

    @POST
    @Path("/mdrequest/accept")
    @Operation(summary = "approve, reject or something depends on the request status", description = "accept the mandays request")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response acceptMandaysRequest(@Parameter(description = "request: MandaysRequestDTO, result: MandaysRequestDTO", required = true)
                                       ServiceRequest<MandaysRequestDTO> request);

}
