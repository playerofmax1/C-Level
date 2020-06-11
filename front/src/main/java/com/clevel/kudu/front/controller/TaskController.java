package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.TaskDTO;
import com.clevel.kudu.dto.working.TaskRequest;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.util.FacesUtil;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

@ViewScoped
@Named("taskCtl")
public class TaskController extends AbstractController {
    private List<TaskDTO> taskList;
    private long selectedTaskId;

    private TaskDTO newTask;
    private boolean editMode;

    private Validator validator;

    public TaskController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        loadTaskList();
    }

    private void loadTaskList() {
        log.debug("loadTaskList.");

        TaskRequest taskRequest = new TaskRequest(false,false,true);
        ServiceRequest<TaskRequest> request = new ServiceRequest<>(taskRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().getTaskList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<TaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<TaskDTO>>>() {
            });
            taskList = serviceResponse.getResult();
            log.debug("taskList: {}", taskList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreNewTask() {
        log.debug("onPreNewTask.");
        newTask = new TaskDTO();
        editMode = false;
        validator = new Validator();
    }

    public void onNewTask() {
        log.debug("onNewTask.");

        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newTask.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newTask.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            return;
        }

        //validation pass
        ServiceRequest<TaskDTO> request = new ServiceRequest<>(newTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().newTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TaskDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadTaskList();

        PrimeFaces.current().executeScript("PF('taskDlg').hide();");
    }

    private void loadTaskInfo() {
        log.debug("loadTaskInfo. (selectedTaskId: {})",selectedTaskId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedTaskId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().getTaskInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TaskDTO>>() {
            });
            newTask = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreUpdateTask() {
        log.debug("onPreUpdateTask.");

        loadTaskInfo();
        editMode = true;
        validator = new Validator();
    }

    public void onUpdateTask() {
        log.debug("onUpdateTask. (selectedTaskId: {})",selectedTaskId);

        //validation
        validator = new Validator();

        // empty validation (first priority)
        validator.mustNotBlank("code",newTask.getCode(),"Code can not be empty");
        validator.mustNotBlank("name",newTask.getName(),"Name can not be empty");

        if (validator.isFailed()) {
            FacesUtil.addError(validator.getMessage());
            loadTaskList();
            return;
        }

        ServiceRequest<TaskDTO> request = new ServiceRequest<>(newTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().updateTaskInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TaskDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadTaskList();

        PrimeFaces.current().executeScript("PF('taskDlg').hide();");
    }

    public void onPreDeleteTask() {
        log.debug("onPreDeleteCustomer. (selectedTaskId: {})",selectedTaskId);

        loadTaskInfo();
    }

    public void onDeleteTask() {
        log.debug("onDeleteTask. (selectedTaskId: {})",selectedTaskId);

        ServiceRequest<TaskDTO> request = new ServiceRequest<>(newTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().deleteTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TaskDTO>>() {
            });
            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadTaskList();
    }

    public List<TaskDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskDTO> taskList) {
        this.taskList = taskList;
    }

    public long getSelectedTaskId() {
        return selectedTaskId;
    }

    public void setSelectedTaskId(long selectedTaskId) {
        this.selectedTaskId = selectedTaskId;
    }

    public TaskDTO getNewTask() {
        return newTask;
    }

    public void setNewTask(TaskDTO newTask) {
        this.newTask = newTask;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
