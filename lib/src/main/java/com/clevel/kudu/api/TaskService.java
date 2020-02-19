package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.TaskDTO;
import com.clevel.kudu.dto.working.TaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/task")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Task Resource", description = "everything for Task")
public interface TaskService {
    @POST
    @Path("/new")
    @Operation(summary = "create new task", description = "create new task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newTask(@Parameter(description = "request: TaskDTO, result: TaskDTO", required = true)
                                ServiceRequest<TaskDTO> request);

    @POST
    @Path("/info")
    @Operation(summary = "get task information", description = "get task information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getTaskInfo(@Parameter(description = "request: SimpleDTO, result: TaskDTO", required = true)
                                    ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/update")
    @Operation(summary = "update task information", description = "update task information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateTaskInfo(@Parameter(description = "request: TaskDTO, result: TaskDTO", required = true)
                                       ServiceRequest<TaskDTO> request);

    @POST
    @Path("/list")
    @Operation(summary = "get list all tasks", description = "get list all tasks")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getTaskList(@Parameter(description = "request: TaskRequest, result: List\\<TaskDTO\\>", required = true)
                                    ServiceRequest<TaskRequest> request);

    @POST
    @Path("/delete")
    @Operation(summary = "delete task", description = "delete task")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteTask(@Parameter(description = "request: TaskDTO, result: ServiceResponse", required = true)
                                   ServiceRequest<TaskDTO> request);

}
