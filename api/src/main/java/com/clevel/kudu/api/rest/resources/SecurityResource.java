package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.SecurityService;
import com.clevel.kudu.api.business.SecurityManager;
import com.clevel.kudu.api.exception.*;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.security.*;
import com.clevel.kudu.dto.working.UserDTO;
import com.clevel.kudu.dto.working.UserTimeSheetDTO;
import com.clevel.kudu.dto.working.UserTimeSheetRequest;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.Screen;
import org.slf4j.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public class SecurityResource implements SecurityService {
    @Inject
    private Logger log;
    @Inject
    private Application app;
    @Inject
    private SecurityManager securityManager;

    @Override
    public Response authenticate(ServiceRequest<AuthenticationRequest> request) {
        log.debug("authenticate. (request: {})",request);

        ServiceResponse<AuthenticationResult> response = new ServiceResponse<>();

        AuthenticationRequest dto = request.getRequest();
        try {
            AuthenticationResult result = securityManager.authenticate(dto.getUserName(), dto.getPassword());
            response.setResult(result);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (CredentialException e) {
            log.debug("{}", e.getMessage());
            response = new ServiceResponse<>(APIResponse.FAILED, e.getMessage());
        } catch (ForceChangePwdException f) {
            log.debug("{}", f.getMessage());
            response = new ServiceResponse<>(APIResponse.FORCE_CHANGE_PWD, f.getMessage());
            response.setResult(f.getResult());
        } catch (InActiveStateException e) {
            log.debug("{}", e.getMessage());
            response = new ServiceResponse<>(APIResponse.ACCOUNT_DISABLED, e.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }


    @Override
    public Response changePassword(ServiceRequest<PwdRequest> request) {
        log.debug("changePassword.");

        ServiceResponse response = new ServiceResponse();
        PwdRequest dto = request.getRequest();

        UserDTO after = null;
        try {
            after = securityManager.changePwd(dto.getUserId(),dto.getOldPassword(),dto.getNewPassword());
            response.setApiResponse(APIResponse.SUCCESS);
        }
        catch (RecordNotFoundException | CredentialException re) {
            log.error("", re);
            response = new ServiceResponse<>(APIResponse.FAILED, re.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response resetPassword(ServiceRequest<PwdRequest> request) {
        log.debug("resetPassword.");

        ServiceResponse response = new ServiceResponse();
        PwdRequest dto = request.getRequest();

        UserDTO after = null;
        try {
            after = securityManager.resetPwd(dto.getUserId());
            response.setApiResponse(APIResponse.SUCCESS);
        }
        catch (RecordNotFoundException | EmailException | UnsupportedEncodingException re) {
            log.error("", re);
            response = new ServiceResponse<>(APIResponse.FAILED, re.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response newRole(ServiceRequest<RoleDTO> request) {
        log.debug("newRole. (request: {})", request);

        ServiceResponse<RoleDTO> response = new ServiceResponse<>();
        RoleDTO dto = request.getRequest();

        try {
            RoleDTO roleDTO = securityManager.createNewRole(request.getUserId(), dto);
            response.setResult(roleDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException|ValidationException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getRoleInfo(ServiceRequest<RoleDTO> request) {
        log.debug("getRoleInfo. (request: {})", request);

        ServiceResponse<RoleDTO> response = new ServiceResponse<>();

        try {
            RoleDTO roleDTO = securityManager.getRoleInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(roleDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response updateRole(ServiceRequest<RoleDTO> request) {
        log.debug("updateRole. (request: {})", request);

        ServiceResponse<RoleDTO> response = new ServiceResponse<>();
        RoleDTO before = null;
        RoleDTO after = null;

        try {
            before = securityManager.getRoleInfo(request.getUserId(), request.getRequest().getId());
            securityManager.updateRoleInfo(request.getUserId(), request.getRequest());
            after = securityManager.getRoleInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
            // for comparison purpose
            request.setRequest(before);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (EJBException ejb) {
            if (ejb.getCausedByException() instanceof OptimisticLockException) {
                log.error("{}", ejb.getCausedByException().getMessage());
                response.setResult(before);
                response.setApiResponse(APIResponse.RECORD_UPDATED_BY_OTHER_SESSION);
                response.setMessage(ejb.getCausedByException().getMessage());
            } else {
                response = new ServiceResponse<>(APIResponse.FAILED, ejb.getCausedByException().getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getRoleList(ServiceRequest<SimpleDTO> request) {
        log.debug("getRoleList. (request: {})", request);

        List<RoleDTO> drawerDTOList = Collections.emptyList();
        ServiceResponse<List<RoleDTO>> response = new ServiceResponse<>();

        try {
            drawerDTOList = securityManager.getRoleList();
            response.setResult(drawerDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response deleteRole(ServiceRequest<RoleDTO> request) {
        log.debug("deleteRole. (request: {})", request);

        ServiceResponse<RoleDTO> response = new ServiceResponse<>();

        try {
            securityManager.deleteRole(request.getUserId(), request.getRequest());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (ValidationException ve) {
            log.debug("", ve);
            response = new ServiceResponse<>(ve.getApiResponse(), ve.getMessage());
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getRoleScreenList(ServiceRequest<RoleDTO> request) {
        log.debug("getRoleScreenList. (request: {})", request);

        ServiceResponse<List<Screen>> response = new ServiceResponse<>();

        try {
            RoleScreenRequest roleScreenRequest = securityManager.getRelRoleScreen(request.getUserId(),request.getRequest());
            response.setResult(roleScreenRequest.getScreenList());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response updateRoleScreen(ServiceRequest<RoleScreenRequest> request) {
        log.debug("updateRoleScreen. (request: {})", request);

        ServiceResponse<RoleScreenRequest> response = new ServiceResponse<>();
        RoleScreenRequest before = null;
        RoleScreenRequest after = null;

        try {
            before = securityManager.getRelRoleScreen(request.getUserId(),request.getRequest().getRole());
            securityManager.updateRoleScreen(request.getUserId(), request.getRequest().getRole(), request.getRequest().getScreenList());
            after = securityManager.getRelRoleScreen(request.getUserId(),request.getRequest().getRole());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
            // for comparison purpose
            request.setRequest(before);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (EJBException ejb) {
            if (ejb.getCausedByException() instanceof OptimisticLockException) {
                log.error("{}", ejb.getCausedByException().getMessage());
                response.setResult(before);
                response.setApiResponse(APIResponse.RECORD_UPDATED_BY_OTHER_SESSION);
                response.setMessage(ejb.getCausedByException().getMessage());
            } else {
                response = new ServiceResponse<>(APIResponse.FAILED, ejb.getCausedByException().getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getRoleFunctionList(ServiceRequest<RoleDTO> request) {
        log.debug("getRoleFunctionList. (request: {})", request);

        ServiceResponse<List<Function>> response = new ServiceResponse<>();

        try {
            RoleFunctionRequest roleScreenRequest = securityManager.getRelRoleFunction(request.getUserId(),request.getRequest());
            response.setResult(roleScreenRequest.getFunctionList());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response updateRoleFunction(ServiceRequest<RoleFunctionRequest> request) {
        log.debug("updateRoleFunction. (request: {})", request);

        ServiceResponse<RoleFunctionRequest> response = new ServiceResponse<>();
        RoleFunctionRequest before = null;
        RoleFunctionRequest after = null;

        try {
            before = securityManager.getRelRoleFunction(request.getUserId(),request.getRequest().getRole());
            securityManager.updateRoleFunction(request.getUserId(), request.getRequest().getRole(), request.getRequest().getFunctionList());
            after = securityManager.getRelRoleFunction(request.getUserId(),request.getRequest().getRole());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
            // for comparison purpose
            request.setRequest(before);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (EJBException ejb) {
            if (ejb.getCausedByException() instanceof OptimisticLockException) {
                log.error("{}", ejb.getCausedByException().getMessage());
                response.setResult(before);
                response.setApiResponse(APIResponse.RECORD_UPDATED_BY_OTHER_SESSION);
                response.setMessage(ejb.getCausedByException().getMessage());
            } else {
                response = new ServiceResponse<>(APIResponse.FAILED, ejb.getCausedByException().getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response newUser(ServiceRequest<UserDTO> request) {
        log.debug("newUser. (request: {})", request);

        ServiceResponse<UserDTO> response = new ServiceResponse<>();
        UserDTO dto = request.getRequest();

        try {
            UserDTO userDTO = securityManager.createNewUser(request.getUserId(), dto);
            response.setResult(userDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException|ValidationException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getUserInfo(ServiceRequest<SimpleDTO> request) {
        log.debug("getUserInfo. (request: {})", request);

        ServiceResponse<UserDTO> response = new ServiceResponse<>();

        try {
            UserDTO userDTO = securityManager.getUserInfoById(request.getUserId(), request.getRequest().getId());
            response.setResult(userDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response updateUserInfo(ServiceRequest<UserDTO> request) {
        log.debug("updateUserInfo. (request: {})", request);

        ServiceResponse<UserDTO> response = new ServiceResponse<>();
        UserDTO before = null;
        UserDTO after = null;

        try {
            before = securityManager.getUserInfoById(request.getUserId(), request.getRequest().getId());
            securityManager.updateUserInfo(request.getUserId(), request.getRequest());
            after = securityManager.getUserInfoById(request.getUserId(), request.getRequest().getId());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
            // for comparison purpose
            request.setRequest(before);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (EJBException ejb) {
            if (ejb.getCausedByException() instanceof OptimisticLockException) {
                log.error("{}", ejb.getCausedByException().getMessage());
                response.setResult(before);
                response.setApiResponse(APIResponse.RECORD_UPDATED_BY_OTHER_SESSION);
                response.setMessage(ejb.getCausedByException().getMessage());
            } else {
                response = new ServiceResponse<>(APIResponse.FAILED, ejb.getCausedByException().getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getUserViewTS(ServiceRequest<SimpleDTO> request) {
        log.debug("getUserViewTS. (request: {})", request);

        ServiceResponse<List<UserTimeSheetDTO>> response = new ServiceResponse<>();

        try {
            List<UserTimeSheetDTO> userTimeSheetDTOList = securityManager.getUserViewTS(request.getUserId(), request.getRequest().getId());
            response.setResult(userTimeSheetDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response updateUserViewTS(ServiceRequest<UserTimeSheetRequest> request) {
        log.debug("updateUserViewTS. (request: {})", request);

        ServiceResponse<UserDTO> response = new ServiceResponse<>();
        UserDTO before = null;

        try {
            securityManager.updateUserViewTS(request.getUserId(),request.getRequest().getOwnerUserId(), request.getRequest().getUserTimeSheetList());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (EJBException ejb) {
            if (ejb.getCausedByException() instanceof OptimisticLockException) {
                log.error("{}", ejb.getCausedByException().getMessage());
                response.setResult(before);
                response.setApiResponse(APIResponse.RECORD_UPDATED_BY_OTHER_SESSION);
                response.setMessage(ejb.getCausedByException().getMessage());
            } else {
                response = new ServiceResponse<>(APIResponse.FAILED, ejb.getCausedByException().getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getUserList(ServiceRequest<SimpleDTO> request) {
        log.debug("getUserList. (request: {})", request);

        List<UserDTO> userDTOList = Collections.emptyList();
        ServiceResponse<List<UserDTO>> response = new ServiceResponse<>();

        try {
            userDTOList = securityManager.getUserList(request.getUserId());
            response.setResult(userDTOList);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }
    @Override
    public Response deleteUser(ServiceRequest<UserDTO> request) {
        log.debug("deleteUser. (request: {})", request);

        ServiceResponse<UserDTO> response = new ServiceResponse<>();

        try {
            securityManager.deleteUser(request.getUserId(), request.getRequest());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }


}
