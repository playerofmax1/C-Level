package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.ProjectService;
import com.clevel.kudu.api.business.ProjectManager;
import com.clevel.kudu.api.business.TimeSheetManager;
import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.SystemConfig;
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
    @Inject
    private TimeSheetManager timeSheetManager;

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
            projectDTOList = projectManager.searchProject(request.getUserId(), request.getRequest());
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
            projectTaskDTOList = projectManager.getProjectTaskList(request.getUserId(), request.getRequest().getId());
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
            projectTaskExtDTOList = projectManager.getProjectTaskExtList(request.getUserId(), request.getRequest().getId());
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
            projectTaskDTOList = projectManager.getProjectTaskListForUser(request.getUserId(), request.getRequest().getId());
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
            ProjectTaskDTO projectTaskDTO = projectManager.createNewProjectTask(request.getUserId(), dto, null);
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
            ProjectTaskExtDTO projectTaskExtDTO = projectManager.createExtendProjectTask(request.getUserId(), dto.getProjectTaskDTO(), dto.getProjectTaskExtDTO(), null);
            response.setResult(projectTaskExtDTO);
            response.setApiResponse(APIResponse.SUCCESS);
        } catch (RecordNotFoundException el) {
            log.debug("", el);
            response = new ServiceResponse<>(APIResponse.FAILED, el.getMessage());
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

    @Override
    public Response getMandaysRequestList(ServiceRequest<UserStatusRequest> request) {
        log.debug("getMandaysRequestList(request: {})", request);

        ServiceResponse<MandaysRequestResult> response = new ServiceResponse<>();
        UserStatusRequest userStatusRequest = request.getRequest();
        try {
            MandaysRequestResult mandaysRequestResult = new MandaysRequestResult();

            int year = userStatusRequest.getYear();
            if (year == 0) {
                year = Integer.parseInt(app.getConfig(SystemConfig.PF_YEAR));
                log.debug("{} = {}", SystemConfig.PF_YEAR.name(), year);
            }
            PerformanceYearDTO performanceYear = timeSheetManager.getPerformanceYear(year);
            mandaysRequestResult.setPerformanceYear(performanceYear);

            List<MandaysRequestDTO> mandaysRequestDTOList = projectManager.getMandaysRequestList(userStatusRequest.getUserId(), userStatusRequest.getStatus(), performanceYear.getStartDate(), performanceYear.getEndDate());
            mandaysRequestResult.setMandaysRequestList(mandaysRequestDTOList);

            PerformanceYearDTO nextPerformanceYearDTO = timeSheetManager.getPerformanceYear(year + 1);
            mandaysRequestResult.setHasNextYear(nextPerformanceYearDTO != null);

            PerformanceYearDTO prePerformanceYearDTO = timeSheetManager.getPerformanceYear(year - 1);
            mandaysRequestResult.setHasPreviousYear(prePerformanceYearDTO != null);

            response.setResult(mandaysRequestResult);
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
    public Response newMandaysRequest(ServiceRequest<MandaysRequestDTO> request) {
        log.debug("newMandaysRequest(request: {})", request);

        ServiceResponse<MandaysRequestDTO> response = new ServiceResponse<>();
        MandaysRequestDTO newMandaysRequest = request.getRequest();
        try {
            MandaysRequestDTO mandaysRequestResult = projectManager.createMandaysRequest(request.getUserId(), newMandaysRequest);
            if (mandaysRequestResult == null) {
                response.setApiResponse(APIResponse.FAILED);
                response.setMessage("Incompatible data, please check Type and ProjectTask!");
            }else {
                response.setResult(mandaysRequestResult);
                response.setApiResponse(APIResponse.SUCCESS);
            }
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
    public Response acceptMandaysRequest(ServiceRequest<MandaysRequestDTO> request) {
        log.debug("acceptMandaysRequest(request: {})", request);

        ServiceResponse<MandaysRequestDTO> response = new ServiceResponse<>();
        MandaysRequestDTO newMandaysRequest = request.getRequest();
        try {
            StringBuffer message = new StringBuffer();
            MandaysRequestDTO mandaysRequestResult = projectManager.acceptMandaysRequest(request.getUserId(), newMandaysRequest, message);
            if (mandaysRequestResult == null) {
                response.setApiResponse(APIResponse.FAILED);
                response.setMessage(message.toString());
            } else {
                response.setResult(mandaysRequestResult);
                response.setApiResponse(APIResponse.SUCCESS);
                response.setMessage(message.toString());
            }
        } catch (RecordNotFoundException | EmailException e1) {
            log.debug("", e1);
            response = new ServiceResponse<>(APIResponse.FAILED, e1.getMessage());
        } catch (Exception e) {
            log.error("", e);
            response = new ServiceResponse<>(APIResponse.EXCEPTION, e.getMessage());
        }

        return Response.ok().entity(response).build();
    }
}
