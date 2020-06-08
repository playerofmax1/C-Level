package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.ConfigDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/system")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "System Resource", description = "system")
public interface SystemService {

    @POST
    @Path("/config/list")
    @Operation(summary = "get all configs", description = "get all configs")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getConfigList(@Parameter(description = "request: SimpleDTO, result: List<ConfigDTO>", required = true)
                                    ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/config/save")
    @Operation(summary = "save all configs", description = "get all configs")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response saveConfigList(@Parameter(description = "request: List<ConfigDTO>, result: List<ConfigDTO>", required = true)
                                    ServiceRequest<List<ConfigDTO>> request);

}
