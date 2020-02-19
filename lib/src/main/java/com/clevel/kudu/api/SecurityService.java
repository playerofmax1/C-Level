package com.clevel.kudu.api;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.security.*;
import com.clevel.kudu.dto.working.UserDTO;
import com.clevel.kudu.dto.working.UserTimeSheetDTO;
import com.clevel.kudu.dto.working.UserTimeSheetRequest;
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

@Path("/security")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Security Resource", description = "everything for security")
public interface SecurityService {
    @POST
    @Path("/authenticate")
    @Operation(summary = "authentication", description = "authenticate user")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response authenticate(@Parameter(description = "request: AuthenticationRequest, result: AuthenticationResult",
            required = true) ServiceRequest<AuthenticationRequest> request);

    @POST
    @Path("/changePwd")
    @Operation(summary = "change user password", description = "change user's password")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response changePassword(@Parameter(description = "request: PwdRequest, result: ServiceResponse", required = true)
                                    ServiceRequest<PwdRequest> request);

    @POST
    @Path("/resetPwd")
    @Operation(summary = "reset user password", description = "reset user's password")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response resetPassword(@Parameter(description = "request: PwdRequest, result: ServiceResponse", required = true)
                                   ServiceRequest<PwdRequest> request);

    @POST
    @Path("/role/new")
    @Operation(summary = "create new role", description = "create new role")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newRole(@Parameter(description = "request: RoleDTO, result: ServiceResponse", required = true)
                             ServiceRequest<RoleDTO> request);

    @POST
    @Path("/role/info")
    @Operation(summary = "get role information", description = "get role information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getRoleInfo(@Parameter(description = "request: RoleDTO, result: RoleDTO", required = true)
                                 ServiceRequest<RoleDTO> request);

    @POST
    @Path("/role/update")
    @Operation(summary = "update role information", description = "update role information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateRole(@Parameter(description = "request: RoleDTO, result: RoleDTO", required = true)
                                ServiceRequest<RoleDTO> request);

    @POST
    @Path("/role/list")
    @Operation(summary = "get list all role", description = "get list all role")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getRoleList(@Parameter(description = "request: SimpleDTO, result: List\\<RoleDTO\\>", required = true)
                                 ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/role/delete")
    @Operation(summary = "delete role", description = "delete role")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteRole(@Parameter(description = "request: RoleDTO, result: ServiceResponse", required = true)
                                ServiceRequest<RoleDTO> request);

    @POST
    @Path("/role/screen/list")
    @Operation(summary = "get role screen list", description = "get role screen list")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getRoleScreenList(@Parameter(description = "request: RoleDTO, result: List\\<Screen\\>", required = true)
                                       ServiceRequest<RoleDTO> request);

    @POST
    @Path("/role/screen/update")
    @Operation(summary = "update role screen", description = "update role screen")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateRoleScreen(@Parameter(description = "request: RoleScreenRequest, result: ServiceResponse", required = true)
                                      ServiceRequest<RoleScreenRequest> request);

    @POST
    @Path("/role/function/list")
    @Operation(summary = "get role function list", description = "get role function list")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getRoleFunctionList(@Parameter(description = "request: RoleDTO, result: List\\<Function\\>", required = true)
                                         ServiceRequest<RoleDTO> request);

    @POST
    @Path("/role/function/update")
    @Operation(summary = "update role function", description = "update role function")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateRoleFunction(@Parameter(description = "request: RoleFunctionRequest, result: ServiceResponse", required = true)
                                        ServiceRequest<RoleFunctionRequest> request);

    @POST
    @Path("/user/new")
    @Operation(summary = "create new user", description = "create new user")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response newUser(@Parameter(description = "request: UserDTO, result: UserDTO", required = true)
                             ServiceRequest<UserDTO> request);

    @POST
    @Path("/user/info")
    @Operation(summary = "get user information", description = "get user information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getUserInfo(@Parameter(description = "request: SimpleDTO, result: UserDTO", required = true)
                                 ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/user/update")
    @Operation(summary = "update user information", description = "update user information")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateUserInfo(@Parameter(description = "request: UserDTO, result: UserDTO", required = true)
                                    ServiceRequest<UserDTO> request);


    @POST
    @Path("/user/view/ts")
    @Operation(summary = "get list user view time sheet", description = "get list user view time sheet")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getUserViewTS(@Parameter(description = "request: SimpleDTO, result: List\\<UserTimeSheetDTO\\>", required = true)
                                      ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/user/update/view/ts")
    @Operation(summary = "update user view time sheet", description = "update user view time sheet")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response updateUserViewTS(@Parameter(description = "request: List\\<UserTimeSheetRequest\\>, result: ServiceResponse", required = true)
                                    ServiceRequest<UserTimeSheetRequest> request);

//    @POST
//    @Path("/user/search")
//    @Operation(summary = "search user", description = "search user")
//    @ApiResponse(responseCode = "200", description = "SUCCESS")
//    Response searchUser(@Parameter(description = "request: UserSearchRequest, result: List\\<UserDTO\\>", required = true)
//                                ServiceRequest<UserSearchRequest> request);

    @POST
    @Path("/user/list")
    @Operation(summary = "get list all users", description = "get list all users")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response getUserList(@Parameter(description = "request: SimpleDTO, result: List\\<UserDTO\\>", required = true)
                                 ServiceRequest<SimpleDTO> request);

    @POST
    @Path("/user/delete")
    @Operation(summary = "delete user", description = "delete user")
    @ApiResponse(responseCode = "200", description = "SUCCESS")
    Response deleteUser(@Parameter(description = "request: UserDTO, result: ServiceResponse", required = true)
                                ServiceRequest<UserDTO> request);

}
