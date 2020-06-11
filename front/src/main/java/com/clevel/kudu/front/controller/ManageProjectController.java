package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.util.FacesUtil;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static com.clevel.kudu.util.LookupUtil.getObjById;

@ViewScoped
@Named("managePrjCtl")
public class ManageProjectController extends AbstractController {
    private long projectId;
    private ProjectDTO project;
    private CostDTO projectCost;

    private List<TaskDTO> taskList;
    private long selectedTaskId;

    private List<ProjectTaskExtDTO> taskExtList;

    private List<UserDTO> userList;
    private long selectedUserId;

    private List<ProjectTaskDTO> projectTaskList;
    private ProjectTaskDTO newProjectTask;
    private ProjectTaskExtDTO newExtTask;
    private boolean editMode;

    private long selectProjectTaskId;

    private Validator validator;

    private BigDecimal sumPlanMD;
    private BigDecimal sumExtendMD;
    private BigDecimal sumTotalMD;
    private BigDecimal sumActualMD;

    public ManageProjectController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");
        try {
            projectId = (long) FacesUtil.getSession().getAttribute("projectId");
        } catch (Exception e) {
            log.error("no project id.");
            FacesUtil.redirect("/site/project.jsf");
        }
//        FacesUtil.getSession().removeAttribute("projectId");

        loadProject();
        loadProjectCost();
        loadProjectTask();
    }

    private void loadProject() {
        log.debug("loadProject. (projectId: {})", projectId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(projectId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectDTO>>() {
            });
            project = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadProjectCost() {
        /*log.debug("loadProjectCost. (projectId: {})", projectId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(projectId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectCost(request);
        if (response.getStatus() == 200) {
            ServiceResponse<CostDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<CostDTO>>() {
            });
            projectCost = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }*/
    }

    private void loadProjectTask() {
        log.debug("loadProjectTask. (projectId: {})", projectId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(projectId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectTaskList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectTaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectTaskDTO>>>() {
            });
            projectTaskList = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
        sumMD();
    }

    private void sumMD() {
        log.debug("sumMD.");
        sumPlanMD = BigDecimal.ZERO;
        sumExtendMD = BigDecimal.ZERO;
        sumTotalMD = BigDecimal.ZERO;
        sumActualMD = BigDecimal.ZERO;
        for (ProjectTaskDTO pt : projectTaskList) {
            sumPlanMD = sumPlanMD.add(pt.getPlanMD());
            sumExtendMD = sumExtendMD.add(pt.getExtendMD());
            sumTotalMD = sumTotalMD.add(pt.getTotalMD());
            sumActualMD = sumActualMD.add(pt.getActualMD());
        }
        project.setTotalPlanMD(sumTotalMD);
    }

    private void loadProjectTaskExt(long projectTaskExtId) {
        log.debug("loadProjectTaskExt. (projectTaskExtId: {})", projectTaskExtId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(projectTaskExtId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectTaskExtList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectTaskExtDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectTaskExtDTO>>>() {
            });
            taskExtList = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadTask() {
        log.debug("loadTask.");

        TaskRequest taskRequest = new TaskRequest(true, false, false);
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

    private void loadUser() {
        log.debug("loadUser.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO());
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserDTO>>>() {
            });
            userList = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreAddNew() {
        log.debug("onPreAddNew.");
        newProjectTask = new ProjectTaskDTO();
        newProjectTask.setAmdCalculation(true);
        editMode = false;

        newProjectTask.setProject(project);
        newProjectTask.setPlanMDDuration(Duration.ZERO);

        loadTask();
        if (!taskList.isEmpty()) {
            newProjectTask.setTask(taskList.get(0));
            selectedTaskId = newProjectTask.getTask().getId();
        }

        loadUser();
        if (!userList.isEmpty()) {
            newProjectTask.setUser(userList.get(0));
            selectedUserId = newProjectTask.getUser().getId();
        }
    }

    public void onChangeTask() {
        log.debug("onChangeTask. (selectTaskId: {})", selectedTaskId);
        if (!taskList.isEmpty()) {
            newProjectTask.setTask(getObjById(taskList, selectedTaskId));
        }
    }

    public void onChangeUser() {
        log.debug("onChangeUser. (selectUserId: {})", selectedUserId);
        if (!userList.isEmpty()) {
            newProjectTask.setUser(getObjById(userList, selectedUserId));
        }
    }

    public void onAddNew() {
        log.debug("onAddNew. (newProjectTask: {})", newProjectTask);

        // Validate Plan-MD > 0
        if (newProjectTask.getPlanMDDuration().compareTo(Duration.ZERO) == 0) {
            log.debug("Plan-MD is equal to 0.");
            FacesUtil.addError("Plan-MD is equal to 0!");
            return;
        }

        ServiceRequest<ProjectTaskDTO> request = new ServiceRequest<>(newProjectTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().newProjectTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectTaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectTaskDTO>>() {
            });

            loadProjectTask();

            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadProjectCost();
        PrimeFaces.current().executeScript("PF('projectTaskDlg').hide();");
    }

    private void loadProjectTaskInfo(ProjectTaskDTO projectTask) {
        log.debug("loadProjectTaskInfo. (projectTask: {})", projectTask);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(projectTask.getId()));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().getProjectTaskInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectTaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectTaskDTO>>() {
            });
            newProjectTask = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreEdit(ProjectTaskDTO projectTask) {
        log.debug("onPreEdit. (projectTask: {})", projectTask);

        loadProjectTaskInfo(projectTask);

        loadTask();
        selectedTaskId = projectTask.getTask().getId();
        loadUser();
        selectedUserId = projectTask.getUser().getId();
        editMode = true;
    }

    public void onUpdate(ProjectTaskDTO projectTask) {
        log.debug("onUpdate. (projectTask: {})", projectTask);

        ServiceRequest<ProjectTaskDTO> request = new ServiceRequest<>(newProjectTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().updateProjectTaskInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectTaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectTaskDTO>>() {
            });

            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        PrimeFaces.current().executeScript("PF('projectTaskDlg').hide();");
    }

    public void onPreExtend(ProjectTaskDTO projectTask) {
        log.debug("onPreEdit. (projectTask: {})", projectTask);
        newProjectTask = projectTask;
        editMode = false;

        newExtTask = new ProjectTaskExtDTO();
        newExtTask.setExtendMDDuration(Duration.ZERO);
        newExtTask.setDescription("");

        loadProjectTaskExt(projectTask.getId());
    }

    public void onExtendManDay() {
        log.debug("onExtendManDay. (newExtTask: {})", newExtTask);

        ProjectTaskExtRequest extRequest = new ProjectTaskExtRequest();
        extRequest.setProjectTaskDTO(newProjectTask);
        extRequest.setProjectTaskExtDTO(newExtTask);
        ServiceRequest<ProjectTaskExtRequest> request = new ServiceRequest<>(extRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().newExtProjectTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse<ProjectTaskDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<ProjectTaskDTO>>() {
            });

            loadProjectTask();

            FacesUtil.addInfo(serviceResponse.getApiResponse().description());
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

        loadProjectCost();
        PrimeFaces.current().executeScript("PF('extendMDDlg').hide();");
    }

    public void onPreDeleteProjectTask(ProjectTaskDTO projectTask) {
        log.debug("onPreDeleteProjectTask. (projectTask: {})", projectTask);

        loadProjectTaskInfo(projectTask);
    }

    public void onDeleteProjectTask() {
        log.debug("onDeleteProjectTask. (projectTask: {})", newProjectTask);

        ServiceRequest<ProjectTaskDTO> request = new ServiceRequest<>(newProjectTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().deleteProjectTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });

            switch (serviceResponse.getApiResponse()) {
                case SUCCESS:
                    FacesUtil.addInfo(serviceResponse.getApiResponse().description());

                    loadProjectTask();
                    loadProjectCost();
                    PrimeFaces.current().executeScript("PF('deleteProjectTaskDlg').hide();");
                    break;
                case FAILED:
                case EXCEPTION:
                    FacesUtil.addError(serviceResponse.getMessage());
                    break;
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreLockProjectTask(ProjectTaskDTO projectTask) {
        log.debug("onPreLockProjectTask. (projectTask: {})", projectTask);
        newProjectTask = projectTask;
    }

    public void onLockProjectTask() {
        log.debug("onLockProjectTask. (projectTask: {})", newProjectTask);

        ServiceRequest<ProjectTaskDTO> request = new ServiceRequest<>(newProjectTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().lockProjectTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });

            switch (serviceResponse.getApiResponse()) {
                case SUCCESS:
                    FacesUtil.addInfo(serviceResponse.getApiResponse().description());

                    loadProjectTask();
                    PrimeFaces.current().executeScript("PF('lockProjectTaskDlg').hide();");
                    break;
                case FAILED:
                case EXCEPTION:
                    FacesUtil.addError(serviceResponse.getMessage());
                    break;
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onPreUnlockProjectTask(ProjectTaskDTO projectTask) {
        log.debug("onPreUnlockProjectTask. (projectTask: {})", projectTask);
        newProjectTask = projectTask;
    }

    public void onUnlockProjectTask() {
        log.debug("onUnlockProjectTask. (projectTask: {})", newProjectTask);

        ServiceRequest<ProjectTaskDTO> request = new ServiceRequest<>(newProjectTask);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getProjectResource().unlockProjectTask(request);
        if (response.getStatus() == 200) {
            ServiceResponse serviceResponse = response.readEntity(new GenericType<ServiceResponse>() {
            });

            switch (serviceResponse.getApiResponse()) {
                case SUCCESS:
                    FacesUtil.addInfo(serviceResponse.getApiResponse().description());

                    loadProjectTask();
                    loadProjectCost();
                    PrimeFaces.current().executeScript("PF('unLockProjectTaskDlg').hide();");
                    break;
                case FAILED:
                case EXCEPTION:
                    FacesUtil.addError(serviceResponse.getMessage());
                    break;
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onHundredPlusClicked() {
        newProjectTask.setPlanMDDuration(Duration.ofHours(100));
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
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

    public List<ProjectTaskExtDTO> getTaskExtList() {
        return taskExtList;
    }

    public void setTaskExtList(List<ProjectTaskExtDTO> taskExtList) {
        this.taskExtList = taskExtList;
    }

    public List<UserDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDTO> userList) {
        this.userList = userList;
    }

    public long getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    public List<ProjectTaskDTO> getProjectTaskList() {
        return projectTaskList;
    }

    public void setProjectTaskList(List<ProjectTaskDTO> projectTaskList) {
        this.projectTaskList = projectTaskList;
    }

    public ProjectTaskDTO getNewProjectTask() {
        return newProjectTask;
    }

    public void setNewProjectTask(ProjectTaskDTO newProjectTask) {
        this.newProjectTask = newProjectTask;
    }

    public ProjectTaskExtDTO getNewExtTask() {
        return newExtTask;
    }

    public void setNewExtTask(ProjectTaskExtDTO newExtTask) {
        this.newExtTask = newExtTask;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public long getSelectProjectTaskId() {
        return selectProjectTaskId;
    }

    public void setSelectProjectTaskId(long selectProjectTaskId) {
        this.selectProjectTaskId = selectProjectTaskId;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public BigDecimal getSumPlanMD() {
        return sumPlanMD;
    }

    public void setSumPlanMD(BigDecimal sumPlanMD) {
        this.sumPlanMD = sumPlanMD;
    }

    public BigDecimal getSumExtendMD() {
        return sumExtendMD;
    }

    public void setSumExtendMD(BigDecimal sumExtendMD) {
        this.sumExtendMD = sumExtendMD;
    }

    public BigDecimal getSumTotalMD() {
        return sumTotalMD;
    }

    public void setSumTotalMD(BigDecimal sumTotalMD) {
        this.sumTotalMD = sumTotalMD;
    }

    public BigDecimal getSumActualMD() {
        return sumActualMD;
    }

    public void setSumActualMD(BigDecimal sumActualMD) {
        this.sumActualMD = sumActualMD;
    }

    public CostDTO getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(CostDTO projectCost) {
        this.projectCost = projectCost;
    }
}
