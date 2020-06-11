package com.clevel.kudu.api.rest.resources;

import com.clevel.kudu.api.TaskService;
import com.clevel.kudu.api.business.TaskManager;
import com.clevel.kudu.api.exception.RecordNotFoundException;
import com.clevel.kudu.api.exception.ValidationException;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.TaskDTO;
import com.clevel.kudu.dto.working.TaskRequest;
import com.clevel.kudu.model.APIResponse;
import org.slf4j.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

public class TaskResource implements TaskService {
    @Inject
    private Logger log;
    @Inject
    private Application app;

    @Inject
    private TaskManager taskManager;

    @Override
    public Response newTask(ServiceRequest<TaskDTO> request) {
        log.debug("newTask. (request: {})", request);

        ServiceResponse<TaskDTO> response = new ServiceResponse<>();
        TaskDTO dto = request.getRequest();

        try {
            TaskDTO taskDTO = taskManager.createNewTask(request.getUserId(), dto);
            response.setResult(taskDTO);
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
    public Response getTaskInfo(ServiceRequest<SimpleDTO> request) {
        log.debug("getTaskInfo. (request: {})", request);

        ServiceResponse<TaskDTO> response = new ServiceResponse<>();

        try {
            TaskDTO taskDTO = taskManager.getTaskInfo(request.getUserId(), request.getRequest().getId());
            response.setResult(taskDTO);
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
    public Response updateTaskInfo(ServiceRequest<TaskDTO> request) {
        log.debug("updateTaskInfo. (request: {})", request);

        ServiceResponse<TaskDTO> response = new ServiceResponse<>();
        TaskDTO before = null;
        TaskDTO after = null;

        try {
            before = taskManager.getTaskInfo(request.getUserId(), request.getRequest().getId());
            taskManager.updateTaskInfo(request.getUserId(), request.getRequest());
            after = taskManager.getTaskInfo(request.getUserId(), request.getRequest().getId());
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
    public Response getTaskList(ServiceRequest<TaskRequest> request) {
        log.debug("getTaskList. (request: {})", request);

        List<TaskDTO> taskDTOList = Collections.emptyList();
        ServiceResponse<List<TaskDTO>> response = new ServiceResponse<>();

        try {
            taskDTOList = taskManager.getTaskList(request.getUserId(),
                    request.getRequest().isChargeable(),
                    request.getRequest().isNonChargeAble(), request.getRequest().isAll());
            response.setResult(taskDTOList);
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
    public Response deleteTask(ServiceRequest<TaskDTO> request) {
        log.debug("deleteTask. (request: {})", request);

        ServiceResponse<TaskDTO> response = new ServiceResponse<>();

        try {
            taskManager.deleteTask(request.getUserId(), request.getRequest());
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
