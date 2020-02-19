package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.ProjectService;
import com.clevel.kudu.api.business.ProjectManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import org.slf4j.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

public class ProjectResource implements ProjectService {
    @Inject
    private Logger log;
    @Inject
    private Application app;

    @Inject
    private ProjectManager projectManager;

    @Override
    public Response newProject(ServiceRequest<ProjectDTO> request) {
        log.debug("newProject. (request: {})", request);

        ServiceResponse<ProjectDTO> response = new ServiceResponse<>();
        ProjectDTO dto = request.getRequest();

        try {
            ProjectDTO projectDTO = projectManager.createNewProject(request.getUserId(), dto);
            response.setResult(projectDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException | ValidationException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response getProjectInfo(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectInfo. (request: {})", request);

        ServiceResponse<ProjectDTO> response = new ServiceResponse<>();

        try {
            ProjectDTO projectDTO = projectManager.getProjectInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(projectDTO);
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
    public Response updateProjectInfo(ServiceRequest<ProjectDTO> request) {
        log.debug("updateProjectInfo. (request: {})", request);

        ServiceResponse<ProjectDTO> response = new ServiceResponse<>();
        ProjectDTO before = null;
        ProjectDTO after = null;

        try {
//            before = projectManager.getProjectInfo(request.getUserId(), request.getRequest().getId());
            projectManager.updateProjectInfo(request.getUserId(), request.getRequest());
//            after = projectManager.getProjectInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(after);
            response.setApiResponse(APIResponse.SUCCESS);
            // for comparison purpose
//            request.setRequest(before);
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
    public Response getProjectList(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectList. (request: {})", request);

        List<ProjectDTO> projectDTOList = Collections.emptyList();
        ServiceResponse<List<ProjectDTO>> response = new ServiceResponse<>();

        try {
            projectDTOList = projectManager.getProjectList(request.getUserId());
            response.setResult(projectDTOList);
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
    public Response closeProject(ServiceRequest<ProjectDTO> request) {
        log.debug("closeProject. (request: {})", request);

        ServiceResponse<ProjectDTO> response = new ServiceResponse<>();

        try {
            projectManager.closeProject(request.getUserId(), request.getRequest());
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
    public Response deleteProject(ServiceRequest<ProjectDTO> request) {
        log.debug("deleteProject. (request: {})", request);

        ServiceResponse<ProjectDTO> response = new ServiceResponse<>();

        try {
            projectManager.deleteProject(request.getUserId(), request.getRequest());
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
    public Response searchProject(ServiceRequest<SearchRequest> request) {
        log.debug("searchProject. (request: {})", request);

        List<ProjectDTO> projectDTOList = Collections.emptyList();
        ServiceResponse<List<ProjectDTO>> response = new ServiceResponse<>();

        try {
            projectDTOList = projectManager.searchProject(request.getUserId(),request.getRequest());
            response.setResult(projectDTOList);
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
    public Response getProjectTaskList(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectTaskList. (request: {})", request);

        List<ProjectTaskDTO> projectTaskDTOList = Collections.emptyList();
        ServiceResponse<List<ProjectTaskDTO>> response = new ServiceResponse<>();

        try {
            projectTaskDTOList = projectManager.getProjectTaskList(request.getUserId(),request.getRequest().getId());
            response.setResult(projectTaskDTOList);
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
    public Response getProjectTaskInfo(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectTaskInfo. (request: {})", request);

        ServiceResponse<ProjectTaskDTO> response = new ServiceResponse<>();

        try {
            ProjectTaskDTO projectTaskDTO = projectManager.getProjectTaskInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(projectTaskDTO);
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
    public Response getProjectTaskExtList(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectTaskExtList. (request: {})", request);

        List<ProjectTaskExtDTO> projectTaskExtDTOList = Collections.emptyList();
        ServiceResponse<List<ProjectTaskExtDTO>> response = new ServiceResponse<>();

        try {
            projectTaskExtDTOList = projectManager.getProjectTaskExtList(request.getUserId(),request.getRequest().getId());
            response.setResult(projectTaskExtDTOList);
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
    public Response getProjectTaskListForUser(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectTaskListForUser. (request: {})", request);

        List<ProjectTaskDTO> projectTaskDTOList = Collections.emptyList();
        ServiceResponse<List<ProjectTaskDTO>> response = new ServiceResponse<>();

        try {
            projectTaskDTOList = projectManager.getProjectTaskListForUser(request.getUserId(),request.getRequest().getId());
            response.setResult(projectTaskDTOList);
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
    public Response newProjectTask(ServiceRequest<ProjectTaskDTO> request) {
        log.debug("newProjectTask. (request: {})", request);

        ServiceResponse<ProjectTaskDTO> response = new ServiceResponse<>();
        ProjectTaskDTO dto = request.getRequest();

        try {
            ProjectTaskDTO projectTaskDTO = projectManager.createNewProjectTask(request.getUserId(), dto);
            response.setResult(projectTaskDTO);
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
    public Response newExtProjectTask(ServiceRequest<ProjectTaskExtRequest> request) {
        log.debug("newExtProjectTask. (request: {})", request);

        ServiceResponse<ProjectTaskExtDTO> response = new ServiceResponse<>();
        ProjectTaskExtRequest dto = request.getRequest();

        try {
            ProjectTaskExtDTO projectTaskExtDTO = projectManager.createExtendProjectTask(request.getUserId(), dto.getProjectTaskDTO(),dto.getProjectTaskExtDTO());
            response.setResult(projectTaskExtDTO);
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
    public Response updateProjectTaskInfo(ServiceRequest<ProjectTaskDTO> request) {
        log.debug("updateProjectInfo. (request: {})", request);

        ServiceResponse<ProjectTaskDTO> response = new ServiceResponse<>();
        ProjectTaskDTO before = null;
        ProjectTaskDTO after = null;

        try {
            before = projectManager.getProjectTaskInfo(request.getUserId(), request.getRequest().getId());
            projectManager.updateProjectTaskInfo(request.getUserId(), request.getRequest());
            after = projectManager.getProjectTaskInfo(request.getUserId(), request.getRequest().getId());
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
    public Response getProjectCost(ServiceRequest<SimpleDTO> request) {
        log.debug("getProjectCost. (request: {})", request);

        ServiceResponse<CostDTO> response = new ServiceResponse<>();

        try {
            CostDTO costDTO = projectManager.getProjectCost(request.getUserId(), request.getRequest().getId());
            response.setResult(costDTO);
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
    public Response deleteProjectTask(ServiceRequest<ProjectTaskDTO> request) {
        log.debug("deleteProjectTask. (request: {})", request);

        ServiceResponse response = new ServiceResponse();

        try {
            projectManager.deleteProjectTask(request.getUserId(), request.getRequest());
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException | ValidationException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }

    @Override
    public Response lockProjectTask(ServiceRequest<ProjectTaskDTO> request) {
        log.debug("lockProjectTask. (request: {})", request);

        ServiceResponse response = new ServiceResponse();

        try {
            projectManager.lockProjectTask(request.getUserId(), request.getRequest());
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
    public Response unlockProjectTask(ServiceRequest<ProjectTaskDTO> request) {
        log.debug("unLockProjectTask. (request: {})", request);

        ServiceResponse response = new ServiceResponse();

        try {
            projectManager.unlockProjectTask(request.getUserId(), request.getRequest());
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
    public Response reCalculatePercentAMD(ServiceRequest<SimpleDTO> request) {
        log.debug("reCalculatePercentAMD. (request: {})", request);

        ServiceResponse response = new ServiceResponse();

        try {
            projectManager.reCalculationPercentAMD();
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }
}
