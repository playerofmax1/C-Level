package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.front.attributes.MandaysRequestOpenAttributes;
import com.clevel.kudu.front.model.SessionAttribute;
import com.clevel.kudu.front.validation.Validator;
import com.clevel.kudu.model.*;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;
import com.clevel.kudu.util.LookupUtil;
import org.primefaces.component.export.ExcelOptions;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ViewScoped
@Named("mdRequestCtl")
public class MandaysRequestController extends AbstractController {

    @Inject
    PageAccessControl accessControl;

    @Inject
    HttpSession httpSession;

    private boolean approver;

    private List<UserDTO> userList;
    private long selectedUserId;
    private List<RequestStatus> requestStatusList;
    private RequestStatus selectedStatus;
    private long selectedMandaysRequestId;

    private int currentYear;
    private boolean previousEnable;
    private boolean nextEnable;

    private List<MandaysRequestDTO> mandaysRequestDTOList;

    private MandaysRequestDTO newMandaysRequest;
    private List<MandaysRequestType> mandaysRequestTypeList;
    private List<ProjectDTO> projectList;
    private List<ProjectTaskDTO> projectTaskList;
    private List<TaskDTO> taskList;
    private String selectedTaskCode;

    private String br = "<br />";
    private boolean openByAttributes;

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        approver = accessControl.functionEnable(Function.F0005);
        if (approver) {
            selectedUserId = 0;
            selectedStatus = RequestStatus.REQUESTED;
            loadUserList();

        } else /*requester*/ {
            selectedUserId = userDetail.getUserId();
            selectedStatus = null;
        }

        requestStatusList = Arrays.asList(RequestStatus.values());
        mandaysRequestTypeList = Arrays.asList(MandaysRequestType.values());
        resetMandaysRequestDialog();

        loadMandaysRequest();

        onPostCreation();
    }

    private void onPostCreation() {
        String openAttributeName = SessionAttribute.MANDAYS_REQUEST_OPEN.name();
        MandaysRequestOpenAttributes openAttributes = (MandaysRequestOpenAttributes) httpSession.getAttribute(openAttributeName);
        if (openAttributes != null) {
            httpSession.removeAttribute(openAttributeName);

            log.debug("onPostCreation.openAttributes = {}", openAttributes);
            ProjectTaskDTO projectTask = openAttributes.getProjectTask();
            ProjectDTO project = new ProjectDTO();
            project.setId(projectTask.getProject().getId());

            ProjectTaskDTO newProjectTask = new ProjectTaskDTO();
            newProjectTask.setId(projectTask.getId());

            UserDTO user = projectTask.getUser();
            UserDTO newUser = new UserDTO();
            newUser.setId(user.getId());
            newUser.setName(user.getName());
            newUser.setLastName(user.getLastName());

            selectedTaskCode = projectTask.getTask().getCode();

            newMandaysRequest.setType(MandaysRequestType.EXTEND);
            newMandaysRequest.setProject(project);
            newMandaysRequest.setProjectTask(newProjectTask);
            newMandaysRequest.setTask(new TaskDTO());
            newMandaysRequest.setUser(newUser);

            loadProjectTaskList();

            openByAttributes = true;
            return;
        }

        openByAttributes = false;
    }

    public void onSearch() {
        log.debug("onSearch(selectedUserId: {}, selectedStatus: {})", selectedUserId, selectedStatus);
        loadMandaysRequest();
        FacesUtil.actionSuccess();
    }

    public void onPreNewRequest() {
        log.debug("onPreNewRequest.");
        resetMandaysRequestDialog();
        FacesUtil.actionSuccess();
    }

    public void onPrevious() {
        log.debug("onPrevious.");
        loadMandaysRequest();
    }

    public void onNext() {
        log.debug("onNext.");
        loadMandaysRequest();
    }

    public void onChangeRequestType() {
        MandaysRequestType selectedType = newMandaysRequest.getType();
        log.debug("onChangeRequestType(selectedType: {})", selectedType);
        loadProjectList(newMandaysRequest.getUser().getId());

        long selectedProjectId = newMandaysRequest.getProject().getId();
        if (selectedProjectId > 0) {
            ProjectDTO project = LookupUtil.getObjById(projectList, selectedProjectId);
            if (project == null) {
                newMandaysRequest.getProject().setId(0);
                newMandaysRequest.getTask().setId(0);
                newMandaysRequest.getProjectTask().setId(0);
            }
        }

        onChangeProject();
    }

    public void onChangeProject() {
        ProjectDTO selectedProject = newMandaysRequest.getProject();
        log.debug("onChangeProject(selectedProject: {})", selectedProject);

        if (MandaysRequestType.EXTEND.equals(newMandaysRequest.getType())) {
            loadProjectTaskList();
            setProjectTaskByCode(selectedTaskCode);
        } else {
            loadTaskList();
            setTaskByCode(selectedTaskCode);
        }
    }

    public void onChangeTask() {
        long selectedTaskId = newMandaysRequest.getTask().getId();
        log.debug("onChangeTask(selectedTaskId:{})", selectedTaskId);

        TaskDTO selectedTask = LookupUtil.getObjById(taskList, selectedTaskId);
        if (selectedTask == null) {
            log.debug("selectedTaskId({}) not found in taskList.", selectedTaskId);
            return;
        }

        selectedTaskCode = selectedTask.getCode();
        log.debug("selectedTaskId({}).code = {}", selectedTaskId, selectedTaskCode);
    }

    public void onChangeProjectTask() {
        long selectedTaskId = newMandaysRequest.getProjectTask().getId();
        log.debug("onChangeProjectTask(selectedTaskId:{})", selectedTaskId);

        ProjectTaskDTO selectedTask = LookupUtil.getObjById(projectTaskList, selectedTaskId);
        if (selectedTask == null) {
            log.debug("selectedTaskId({}) not found in projectTaskList.", selectedTaskId);
            return;
        }

        selectedTaskCode = selectedTask.getTask().getCode();
        log.debug("selectedTaskId({}).code = {}", selectedTaskId, selectedTaskCode);
    }

    public void onChangeMDDuration() {
        Duration extendMDDuration = newMandaysRequest.getExtendMDDuration();
        log.debug("onChangeMDDuration(extendMDDuration: {})", extendMDDuration);
        if (extendMDDuration == null) {
            return;
        }

        long minutes = extendMDDuration.toMinutes();
        newMandaysRequest.setExtendMDMinute(minutes);
        log.debug("minutes = {}", minutes);

        BigDecimal mandays = DateTimeUtil.getManDays(minutes);
        newMandaysRequest.setExtendMD(mandays);
        log.debug("mandays = {}", mandays);
    }

    public void onSaveRequest() {
        log.debug("onSaveRequest(newMandaysRequest: {})", newMandaysRequest);

        Validator validator = new Validator();
        validator.mustNotNull("project", newMandaysRequest.getProject(), "Please select Project!");

        if (MandaysRequestType.NEW.equals(newMandaysRequest.getType())) {
            validator.mustNotEquals("task", newMandaysRequest.getTask().getId(), 0, "Please select Task!");
        } else /*EXTEND*/ {
            validator.mustNotEquals("task", newMandaysRequest.getProjectTask().getId(), 0, "Please select Task to extend!");
        }

        validator.mustNotBlank("description", newMandaysRequest.getDescription(), "Description is required as reason to manager!");
        validator.mustInRange("mandays", newMandaysRequest.getExtendMD().doubleValue(), 0.01, 100.00, "Mandays range is 0.01 to 100.00!");

        if (validator.isFailed()) {
            FacesUtil.actionFailed(validator.getMessage());
            return;
        }

        ServiceRequest<MandaysRequestDTO> request = new ServiceRequest<>(newMandaysRequest);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getProjectResource().newMandaysRequest(request);
        if (response.getStatus() != 200) {
            String message = "Call API is failed by connection problem(" + response.getStatus() + ")!";
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        ServiceResponse<MandaysRequestDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<MandaysRequestDTO>>() {
        });
        log.debug("serviceResponse = {}", serviceResponse);
        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        mandaysRequestDTOList.add(serviceResponse.getResult());
        FacesUtil.actionSuccess("Requested.");
    }

    public void onPreApprove() {
        log.debug("onPreApprove(selectedMandaysRequestId: {})", selectedMandaysRequestId);

        newMandaysRequest = LookupUtil.getObjById(mandaysRequestDTOList, selectedMandaysRequestId);
        if (newMandaysRequest == null) {
            FacesUtil.actionFailed("Mandays Request ID(" + selectedMandaysRequestId + ") not found on current list.");
            return;
        }

        loadProjectList(newMandaysRequest.getUser().getId());

        if (MandaysRequestType.NEW.equals(newMandaysRequest.getType())) {
            newMandaysRequest.setProjectTask(new ProjectTaskDTO());

            selectedTaskCode = newMandaysRequest.getTask().getCode();

        } else /*EXTEND*/ {
            ProjectTaskDTO projectTask = newMandaysRequest.getProjectTask();
            ProjectDTO project = new ProjectDTO();
            project.setId(projectTask.getProject().getId());
            newMandaysRequest.setProject(project);
            newMandaysRequest.setTask(new TaskDTO());

            selectedTaskCode = projectTask.getTask().getCode();
        }

        onChangeProject();

        log.debug("newMandaysRequest = {}", newMandaysRequest);
        FacesUtil.actionSuccess();
    }

    public void onApprove() {
        log.debug("onApprove.");
        acceptMandaysRequest(RequestStatus.APPROVED, newMandaysRequest);
    }

    public void onReject() {
        log.debug("onReject.");
        acceptMandaysRequest(RequestStatus.REJECTED, newMandaysRequest);
    }

    private void acceptMandaysRequest(RequestStatus requestStatus, MandaysRequestDTO mandaysRequestDTO) {
        log.debug("acceptMandaysRequest(mandaysRequestDTO: {})", mandaysRequestDTO);

        Validator validator = new Validator();
        validator.mustInRange("mandays", mandaysRequestDTO.getExtendMD().doubleValue(), 0.01, 100.00, "Mandays range is 0.01 to 100.00!");

        if (validator.isFailed()) {
            FacesUtil.actionFailed(validator.getMessage());
            return;
        }

        mandaysRequestDTO.setStatus(requestStatus);

        ServiceRequest<MandaysRequestDTO> request = new ServiceRequest<>(mandaysRequestDTO);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getProjectResource().acceptMandaysRequest(request);
        if (response.getStatus() != 200) {
            String message = "Call API is failed by connection problem(" + response.getStatus() + ")!";
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        ServiceResponse<MandaysRequestDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<MandaysRequestDTO>>() {
        });
        log.debug("serviceResponse = {}", serviceResponse);
        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "loadMandaysRequest is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.actionFailed(message);
            return;
        }

        MandaysRequestDTO result = serviceResponse.getResult();
        MandaysRequestDTO remove = LookupUtil.getObjById(mandaysRequestDTOList, result.getId());
        mandaysRequestDTOList.remove(remove);

        mandaysRequestDTOList.add(result);
        FacesUtil.actionSuccess(serviceResponse.getMessage());
    }

    private void setProjectTaskByCode(String selectedTaskCode) {
        if (selectedTaskCode == null) {
            return;
        }

        long projectTaskId;
        for (ProjectTaskDTO projectTask : projectTaskList) {
            if (projectTask.getTask().getCode().equals(selectedTaskCode)) {
                projectTaskId = projectTask.getId();
                log.debug("selectedTaskCode({}) found in projectTaskList, id={}.", selectedTaskCode, projectTaskId);
                newMandaysRequest.getProjectTask().setId(projectTaskId);
                return;
            }
        }

        log.debug("selectedTaskCode({}) not found in projectTaskList.", selectedTaskCode);
    }

    private void setTaskByCode(String selectedTaskCode) {
        if (selectedTaskCode == null) {
            return;
        }

        if (taskList == null) {
            loadTaskList();
        }

        long taskId;
        for (TaskDTO task : taskList) {
            if (task.getCode().equals(selectedTaskCode)) {
                taskId = task.getId();
                newMandaysRequest.getTask().setId(taskId);
                log.debug("selectedTaskCode({}) found in taskList, id={}.", selectedTaskCode, taskId);
                return;
            }
        }

        log.debug("selectedTaskCode({}) not found in taskList.", selectedTaskCode);
    }

    public void resetMandaysRequestDialog() {
        selectedTaskCode = null;

        newMandaysRequest = new MandaysRequestDTO();
        newMandaysRequest.setType(MandaysRequestType.EXTEND);
        newMandaysRequest.setDescription("");
        newMandaysRequest.setExtendMD(BigDecimal.ZERO);
        newMandaysRequest.setExtendMDDuration(Duration.ZERO);
        newMandaysRequest.setExtendMDMinute(0L);
        newMandaysRequest.setAmdCalculation(false);

        UserDTO user = new UserDTO();
        user.setId(userDetail.getUserId());
        user.setName(userDetail.getName());
        user.setLastName(userDetail.getLastName());
        newMandaysRequest.setUser(user);

        newMandaysRequest.setProjectTask(new ProjectTaskDTO());
        newMandaysRequest.setProject(new ProjectDTO());
        newMandaysRequest.setTask(new TaskDTO());

        loadProjectList(user.getId());
    }

    private void loadUserList() {
        log.debug("loadUserList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(userDetail.getUserId()));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserViewTS(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserTimeSheetDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserTimeSheetDTO>>>() {
            });

            userList = new ArrayList<>();
            UserDTO currentUser = new UserDTO();
            currentUser.setId(userDetail.getUserId());
            currentUser.setName(userDetail.getName());
            currentUser.setLastName(userDetail.getLastName());
            userList.add(currentUser);

            List<UserTimeSheetDTO> userTSList = serviceResponse.getResult();
            for (UserTimeSheetDTO userTimeSheetDTO : userTSList) {
                userList.add(userTimeSheetDTO.getTimeSheetUser());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadProjectList(long selectedUserId) {
        MandaysRequestType selectedType = newMandaysRequest.getType();
        log.debug("loadProjectList(selectedType:{}, selectedUserId:{})", selectedType, selectedUserId);

        Response response;
        if (MandaysRequestType.NEW.equals(selectedType)) {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setStatus(RecordStatus.ACTIVE);
            ServiceRequest<SearchRequest> request = new ServiceRequest<>(searchRequest);
            request.setUserId(userDetail.getUserId());
            response = apiService.getProjectResource().searchProject(request);
        } else /*EXTEND*/ {
            ServiceRequest<SimpleDTO> request;
            request = new ServiceRequest<>(new SimpleDTO(selectedUserId));
            request.setUserId(userDetail.getUserId());
            response = apiService.getTimeSheetResource().getProjectList(request);
        }

        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectDTO>>>() {
            });
            projectList = serviceResponse.getResult();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadProjectTaskList() {
        ProjectDTO selectedProject = newMandaysRequest.getProject();
        log.debug("loadProjectTaskList. (selectedProject: {})", selectedProject);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedProject.getId()));
        request.setUserId(newMandaysRequest.getUser().getId());
        Response response = apiService.getProjectResource().getProjectTaskListForUser(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectTaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectTaskDTO>>>() {
            });
            projectTaskList = serviceResponse.getResult();
            log.debug("loadProjectTaskList found " + projectTaskList.size() + " items");
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadTaskList() {
        ProjectDTO selectedProject = newMandaysRequest.getProject();
        log.debug("loadTaskList. (selectedProject: {})", selectedProject);

        if (taskList != null) {
            return;
        }

        TaskRequest taskRequest = new TaskRequest(true, false, false);
        ServiceRequest<TaskRequest> request = new ServiceRequest<>(taskRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskResource().getTaskList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<TaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<TaskDTO>>>() {
            });
            taskList = serviceResponse.getResult();
            log.debug("loadProjectTaskList found " + taskList.size() + " items");
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public boolean isApprover() {
        return approver;
    }

    private void loadMandaysRequest() {
        log.debug("loadMandaysRequest(selectedUserId: {}, selectedStatus: {})", selectedUserId, selectedStatus);

        UserStatusRequest userStatusRequest = new UserStatusRequest();
        userStatusRequest.setUserId(selectedUserId);
        userStatusRequest.setStatus(selectedStatus);

        ServiceRequest<UserStatusRequest> request = new ServiceRequest<>(userStatusRequest);
        request.setUserId(userDetail.getUserId());

        Response response = apiService.getProjectResource().getMandaysRequestList(request);
        if (response.getStatus() != 200) {
            String message = "Call API is failed by connection problem(" + response.getStatus() + ")!";
            log.debug(message);
            FacesUtil.addError(message);
            return;
        }

        ServiceResponse<MandaysRequestResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<MandaysRequestResult>>() {
        });
        log.debug("serviceResponse = {}", serviceResponse);
        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "loadMandaysRequest is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.addError(message);
            return;
        }

        MandaysRequestResult mandaysRequestResult = serviceResponse.getResult();
        mandaysRequestDTOList = mandaysRequestResult.getMandaysRequestList();
        currentYear = (int) mandaysRequestResult.getPerformanceYear().getYear();

        log.debug("onSearch.mandaysRequestDTOList={}", mandaysRequestDTOList);

        checkNavigationButtonEnable();
    }

    public void checkNavigationButtonEnable() {
        Date lastDate = DateTimeUtil.getLastDateOfMonth(DateTimeUtil.now());

        /*TODO: when time sheet has more than one year @2021++ */

        // check next
        //Date next = DateTimeUtil.getDatePlusMonths(currentMonth, 1);
        //nextEnable = !next.after(lastDate);
        nextEnable = false;

        // check previous
        //Date pre = DateTimeUtil.getDatePlusMonths(currentMonth, -1);
        //previousEnable = !pre.before(tsStartDate);
        previousEnable = false;
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

    public long getSelectedMandaysRequestId() {
        return selectedMandaysRequestId;
    }

    public void setSelectedMandaysRequestId(long selectedMandaysRequestId) {
        this.selectedMandaysRequestId = selectedMandaysRequestId;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public boolean isPreviousEnable() {
        return previousEnable;
    }

    public void setPreviousEnable(boolean previousEnable) {
        this.previousEnable = previousEnable;
    }

    public boolean isNextEnable() {
        return nextEnable;
    }

    public void setNextEnable(boolean nextEnable) {
        this.nextEnable = nextEnable;
    }

    public List<MandaysRequestDTO> getMandaysRequestDTOList() {
        return mandaysRequestDTOList;
    }

    public void setMandaysRequestDTOList(List<MandaysRequestDTO> mandaysRequestDTOList) {
        this.mandaysRequestDTOList = mandaysRequestDTOList;
    }

    public MandaysRequestDTO getNewMandaysRequest() {
        return newMandaysRequest;
    }

    public void setNewMandaysRequest(MandaysRequestDTO newMandaysRequest) {
        this.newMandaysRequest = newMandaysRequest;
    }

    public List<MandaysRequestType> getMandaysRequestTypeList() {
        return mandaysRequestTypeList;
    }

    public void setMandaysRequestTypeList(List<MandaysRequestType> mandaysRequestTypeList) {
        this.mandaysRequestTypeList = mandaysRequestTypeList;
    }

    public List<ProjectDTO> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDTO> projectList) {
        this.projectList = projectList;
    }

    public List<ProjectTaskDTO> getProjectTaskList() {
        return projectTaskList;
    }

    public void setProjectTaskList(List<ProjectTaskDTO> projectTaskList) {
        this.projectTaskList = projectTaskList;
    }

    public List<TaskDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskDTO> taskList) {
        this.taskList = taskList;
    }

    public String getSelectedTaskCode() {
        return selectedTaskCode;
    }

    public void setSelectedTaskCode(String selectedTaskCode) {
        this.selectedTaskCode = selectedTaskCode;
    }

    public List<RequestStatus> getRequestStatusList() {
        return requestStatusList;
    }

    public void setRequestStatusList(List<RequestStatus> requestStatusList) {
        this.requestStatusList = requestStatusList;
    }

    public RequestStatus getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(RequestStatus selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public String getBr() {
        return br;
    }

    public boolean isOpenByAttributes() {
        return openByAttributes;
    }

    public void setOpenByAttributes(boolean openByAttributes) {
        this.openByAttributes = openByAttributes;
    }

    public ExcelOptions getExportExcelOptions() {
        return new ExcelOptions("BOLD", "#FFFFFF", "#6666FF", "10", "", "#000000", "11");
    }

    public String dataExportFileName() {
        String userName = null;
        if (userList != null) {
            for (UserDTO userDTO : userList) {
                if (userDTO.getId() == selectedUserId) {
                    userName = userDTO.getLoginName();
                    log.debug("dataExportFileName() = UserName({})", userName);
                }
            }
        }

        if (userName == null) {
            userName = userDetail.getUserName();
            log.debug("dataExportFileName() = CurrentLoggedInUser({})", userName);
        }

        return userName + "-mandays-request-" + currentYear;
    }

}
