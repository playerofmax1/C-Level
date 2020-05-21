package com.clevel.kudu.front.controller;

import com.clevel.kudu.dto.ServiceRequest;
import com.clevel.kudu.dto.ServiceResponse;
import com.clevel.kudu.dto.SimpleDTO;
import com.clevel.kudu.dto.working.*;
import com.clevel.kudu.model.APIResponse;
import com.clevel.kudu.model.Function;
import com.clevel.kudu.model.TaskType;
import com.clevel.kudu.util.DateTimeUtil;
import com.clevel.kudu.util.FacesUtil;
import org.primefaces.PrimeFaces;
import org.primefaces.component.export.ExcelOptions;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.*;

import static com.clevel.kudu.util.LookupUtil.getObjById;

@ViewScoped
@Named("timeSheetCtl")
public class TimeSheetController extends AbstractController {
    private List<TimeSheetDTO> timeSheetSummaryList;
    private List<TimeSheetDTO> timeSheetList;
    private long selectedTimeSheetId;

    private List<UserDTO> userList;
    private long timeSheetUserId;
    private Boolean viewOnly;

    private TimeSheetDTO detail;

    private List<ProjectDTO> projectList;
    private long selectedProjectId;
    private List<ProjectTaskDTO> projectTaskList;
    private long selectedProjectTaskId;
    private List<TaskDTO> taskList;
    private long selectedTaskId;

    private List<TimeSheetDTO> pjtSummary;

    private ExcelOptions exportExcelOptions;

    private Date currentMonth;
    private boolean previousEnable;
    private boolean nextEnable;
    private Date tsStartDate;

    private UtilizationResult utilization;

    private String chargeDurationButton;

    @Inject
    PageAccessControl accessControl;

    public TimeSheetController() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");

        currentMonth = DateTimeUtil.now();
        nextEnable = false;
        previousEnable = true;
        timeSheetUserId = userDetail.getUserId();
        utilization = new UtilizationResult();

        loadUserInfo();
        loadTask();
        loadTimeSheet();

        if (accessControl.functionEnable(Function.F0002)) {
            loadUserList();
        }
    }

    public void onChangeUser() {
        log.debug("onChangeUser. (timeSheetUserId: {})", timeSheetUserId);
        currentMonth = DateTimeUtil.now();
        loadTimeSheet();
    }

    private void loadUserList() {
        log.debug("loadUserList.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(userDetail.getUserId()));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserViewTS(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<UserTimeSheetDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<UserTimeSheetDTO>>>() {
            });
            List<UserTimeSheetDTO> userTSList = serviceResponse.getResult();
            userList = new ArrayList<>();
            UserDTO currentUser = new UserDTO();
            currentUser.setId(userDetail.getUserId());
            currentUser.setName(userDetail.getName());
            currentUser.setLastName(userDetail.getLastName());
            userList.add(currentUser);
            timeSheetUserId = userDetail.getUserId();
            for (UserTimeSheetDTO u : userTSList) {
                userList.add(u.getTimeSheetUser());
            }
            log.debug("userList: {}", userList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    public void onPrevious() {
        log.debug("onPrevious.");
        currentMonth = DateTimeUtil.getDatePlusMonths(currentMonth, -1);
        loadTimeSheet();
    }

    public void onNext() {
        log.debug("onNext.");
        currentMonth = DateTimeUtil.getDatePlusMonths(currentMonth, 1);
        loadTimeSheet();
    }

    public void onChangeViewOnlyFlag() {
        TimeSheetRequest timeSheetRequest = new TimeSheetRequest();
        timeSheetRequest.setTimeSheetUserId(timeSheetUserId);
        timeSheetRequest.setMonth(currentMonth);

        ServiceRequest<TimeSheetRequest> request = new ServiceRequest<>(timeSheetRequest);
        request.setUserId(timeSheetUserId);

        Response response;
        if (viewOnly) {
            response = apiService.getTimeSheetResource().lockTimeSheet(request);
        } else {
            response = apiService.getTimeSheetResource().unlockTimeSheet(request);
        }

        if (response.getStatus() != 200) {
            String message = "Update ViewOnly flag is failed by connection!";
            log.debug(message);
            FacesUtil.addError(message);
            viewOnly = !viewOnly;
            return;
        }

        ServiceResponse<TimeSheetResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetResult>>() {
        });

        if (serviceResponse.getApiResponse() != APIResponse.SUCCESS) {
            String message = "Update ViewOnly flag is failed! " + serviceResponse.getMessage();
            log.debug(message);
            FacesUtil.addError(message);
            viewOnly = !viewOnly;
            return;
        }

        String message = "Update ViewOnly flag success.";
        log.debug(message);
        FacesUtil.addInfo(message);
    }

    public void checkNavigationButtonEnable() {
        Date lastDate = DateTimeUtil.getLastDateOfMonth(DateTimeUtil.now());

        // check next
        Date next = DateTimeUtil.getDatePlusMonths(currentMonth, 1);
        nextEnable = !next.after(lastDate);

        // check previous
        Date pre = DateTimeUtil.getDatePlusMonths(currentMonth, -1);
        previousEnable = !pre.before(tsStartDate);
    }

    private void loadUserInfo() {
        log.debug("loadUserInfo.");
        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(userDetail.getUserId()));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getSecurityResource().getUserInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UserDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UserDTO>>() {
            });
            UserDTO user = serviceResponse.getResult();
            log.debug("user: {}", user);
            tsStartDate = user.getTsStartDate();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void checkViewOnly(TimeSheetResult result) {

        // time sheet locked
        viewOnly = isCurrentMonthLocked(result);
        if (viewOnly) {
            log.debug("viewOnly: true");
            return;
        }

        // view only if expired
        log.debug("cutoff enable: {}", result.isCutoffEnable());
        boolean cutoffEnable = result.isCutoffEnable();
        boolean timeSheetOwner = userDetail.getUserId() == timeSheetUserId;
        boolean forceEdit = userDetail.getFunctionList().contains(Function.F0003);

        if (result.isCutoffEnable()) {
            Date lastDate = DateTimeUtil.getLastDateOfMonth(currentMonth);
            Date cutOffDate = DateTimeUtil.getDatePlusDays(lastDate, result.getCutoffDate());
            Date currentDate = DateTimeUtil.currentDate();
            log.debug("cutOffDate: {}, currentDate: {}", cutOffDate, currentDate);

            if (cutoffEnable && timeSheetOwner && !forceEdit) {
                if (currentDate.after(cutOffDate)) {
                    log.debug("edit expired.");
                    viewOnly = true;
                    log.debug("[edit expired] viewOnly: {}", viewOnly);
                } else {
                    // view only if not owner
                    viewOnly = false;
                    log.debug("[not time sheet owner] viewOnly: {}", viewOnly);
                }
            } else {
                viewOnly = getViewResult(cutoffEnable, timeSheetOwner, forceEdit);
            }
        } else {
            viewOnly = getViewResult(cutoffEnable, timeSheetOwner, forceEdit);
        }

        log.debug("viewOnly: {}", viewOnly);
    }

    private boolean isCurrentMonthLocked(TimeSheetResult result) {
        List<TimeSheetLockDTO> timeSheetLockList = result.getTimeSheetLockList();
        log.debug("isCurrentMonthLocked.timeSheetLockList: {}", timeSheetLockList);
        return timeSheetLockList.size() > 0;
    }

    private boolean getViewResult(boolean cutoffEnable, boolean timeSheetOwner, boolean forceEdit) {
        log.debug("getViewResult. (cutoffEnable: {}, timeSheetOwner: {}, forceEdit: {})", cutoffEnable, timeSheetOwner, forceEdit);

        return (!cutoffEnable && !timeSheetOwner && !forceEdit) || (cutoffEnable && !timeSheetOwner && !forceEdit);
    }

    private void loadTimeSheet() {
        log.debug("loadTimeSheet. (timeSheetUserId: {})", timeSheetUserId);

        TimeSheetRequest timeSheetRequest = new TimeSheetRequest();
        timeSheetRequest.setTimeSheetUserId(timeSheetUserId);
        timeSheetRequest.setMonth(currentMonth);
        ServiceRequest<TimeSheetRequest> request = new ServiceRequest<>(timeSheetRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getTimeSheet(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetResult>>() {
            });
            TimeSheetResult result = serviceResponse.getResult();

            timeSheetList = result.getTimeSheetList();
            sortList(timeSheetList);

            timeSheetSummaryList = sumHoursOfProject(timeSheetList);
            utilization.setUtilization(result.getUtilization());

            checkNavigationButtonEnable();
            checkViewOnly(result);

        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadTimeSheetInfo(TimeSheetDTO timeSheet) {
        log.debug("loadTimeSheetInfo. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getTimeSheetInfo(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            detail = serviceResponse.getResult();
            log.debug("detail: {}", detail);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadUtilization() {
        log.debug("loadUtilization. (timeSheetUserId: {}, currentMonth: {})", timeSheetUserId, currentMonth);

        UtilizationRequest utilizationRequest = new UtilizationRequest(timeSheetUserId, currentMonth);
        ServiceRequest<UtilizationRequest> request = new ServiceRequest<>(utilizationRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getUtilization(request);
        if (response.getStatus() == 200) {
            ServiceResponse<UtilizationResult> serviceResponse = response.readEntity(new GenericType<ServiceResponse<UtilizationResult>>() {
            });

            utilization = serviceResponse.getResult();
            log.debug("utilization: {}", utilization);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadProject() {
        log.debug("loadProject.");

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(timeSheetUserId));
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().getProjectList(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectDTO>>>() {
            });
            projectList = serviceResponse.getResult();
            log.debug("projectList: {}", projectList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void loadProjectTask() {
        log.debug("loadProjectTask. (selectedProjectId: {})", selectedProjectId);

        ServiceRequest<SimpleDTO> request = new ServiceRequest<>(new SimpleDTO(selectedProjectId));
        request.setUserId(timeSheetUserId);
        Response response = apiService.getProjectResource().getProjectTaskListForUser(request);
        if (response.getStatus() == 200) {
            ServiceResponse<List<ProjectTaskDTO>> serviceResponse = response.readEntity(new GenericType<ServiceResponse<List<ProjectTaskDTO>>>() {
            });
            projectTaskList = serviceResponse.getResult();
            log.debug("projectTaskList: {}", projectTaskList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }

    }

    private void loadTask() {
        log.debug("loadTask. (selectedProjectId: {})", selectedProjectId);

        TaskRequest taskRequest = new TaskRequest(false, true, false);
        ServiceRequest<TaskRequest> request = new ServiceRequest<>(taskRequest);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTaskService().getTaskList(request);
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


    private List<TimeSheetDTO> sumHoursOfProject(List<TimeSheetDTO> timeSheetList) {
        /*HashMap<"ProjectCode.TaskCode", TimeSheetDTO>*/
        HashMap<String, TimeSheetDTO> summaryMap = new HashMap<>();
        TimeSheetDTO currentSummaryTimeSheetDTO;
        ProjectTaskDTO currentProjectTask;
        Duration chargeDuration;
        String mapKey;

        for (TimeSheetDTO timeSheetDTO : timeSheetList) {
            currentProjectTask = timeSheetDTO.getProjectTask();
            if (currentProjectTask == null) {
                continue;
            }
            //log.debug("currentProjectTask = {}", currentProjectTask);

            mapKey = currentProjectTask.getProject().getCode();
            currentSummaryTimeSheetDTO = summaryMap.get(mapKey);
            if (currentSummaryTimeSheetDTO == null) {
                currentSummaryTimeSheetDTO = new TimeSheetDTO();

                /*may be need to clone Project and Task to another object*/
                currentSummaryTimeSheetDTO.setProjectTask(currentProjectTask);
                currentSummaryTimeSheetDTO.setProject(timeSheetDTO.getProject());
                currentSummaryTimeSheetDTO.setChargeDuration(timeSheetDTO.getChargeDuration());

                summaryMap.put(mapKey, currentSummaryTimeSheetDTO);

            } else {
                chargeDuration = currentSummaryTimeSheetDTO.getChargeDuration();
                chargeDuration = chargeDuration.plusMinutes(timeSheetDTO.getChargeDuration().toMinutes());
                currentSummaryTimeSheetDTO.setChargeDuration(chargeDuration);
            }
        }

        ArrayList<TimeSheetDTO> summaryList = new ArrayList<>(summaryMap.values());
        summaryList.sort((o1, o2) -> {
            return o1.getProject().getCode().compareTo(o2.getProject().getCode());
        });

        return summaryList;
    }

    private List<TimeSheetDTO> sumHoursOfProjectTask(List<TimeSheetDTO> timeSheetList) {
        /*HashMap<"ProjectCode.TaskCode", TimeSheetDTO>*/
        HashMap<String, TimeSheetDTO> summaryMap = new HashMap<>();
        TimeSheetDTO currentSummaryTimeSheetDTO;
        ProjectTaskDTO currentProjectTask;
        Duration chargeDuration;
        String mapKey;

        for (TimeSheetDTO timeSheetDTO : timeSheetList) {
            currentProjectTask = timeSheetDTO.getProjectTask();
            if (currentProjectTask == null) {
                continue;
            }
            //log.debug("currentProjectTask = {}", currentProjectTask);

            mapKey = currentProjectTask.getProject().getCode() + "." + currentProjectTask.getTask().getCode();
            currentSummaryTimeSheetDTO = summaryMap.get(mapKey);
            if (currentSummaryTimeSheetDTO == null) {
                currentSummaryTimeSheetDTO = new TimeSheetDTO();

                /*may be need to clone Project and Task to another object*/
                currentSummaryTimeSheetDTO.setProjectTask(currentProjectTask);
                currentSummaryTimeSheetDTO.setProject(timeSheetDTO.getProject());
                currentSummaryTimeSheetDTO.setChargeDuration(timeSheetDTO.getChargeDuration());

                summaryMap.put(mapKey, currentSummaryTimeSheetDTO);

            } else {
                chargeDuration = currentSummaryTimeSheetDTO.getChargeDuration();
                chargeDuration = chargeDuration.plusMinutes(timeSheetDTO.getChargeDuration().toMinutes());
                currentSummaryTimeSheetDTO.setChargeDuration(chargeDuration);
            }
        }

        ArrayList<TimeSheetDTO> summaryList = new ArrayList<>(summaryMap.values());
        summaryList.sort((o1, o2) -> {
            int zero = o1.getProject().getCustomer().getName().compareTo(o2.getProject().getCustomer().getName());
            if (zero != 0) return zero;

            zero = o1.getProject().getCode().compareTo(o2.getProject().getCode());
            if (zero != 0) return zero;

            return o1.getProjectTask().getTask().getCode().compareTo(o2.getProjectTask().getTask().getCode());
        });

        return summaryList;
    }

    public void onUpdateTimeInOut() {
        log.debug("onUpdateTimeInOut. (detail: {})", detail);
    }

    public void onChangeProject(TimeSheetDTO timeSheet) {
        log.debug("onChangeProject. (selectedProjectId: {})", selectedProjectId);

        timeSheet.setProject(getObjById(projectList, selectedProjectId));
        timeSheet.setTask(null);
        timeSheet.setChargeDuration(Duration.ZERO);
        timeSheet.setDescription("");
        log.debug("timeSheet: {}", timeSheet);
        onSave(timeSheet);
    }

    public void onChangeProjectInDetail() {
        log.debug("onChangeProjectInDetail. (selectedProjectId: {})", selectedProjectId);
        detail.setProject(getObjById(projectList, selectedProjectId));
        detail.setTask(null);
        selectedProjectTaskId = 0;
        detail.setChargeDuration(Duration.ZERO);
        detail.setDescription("");
        //reset task
        detail.setProjectTask(null);
        detail.setTask(null);

        loadProjectTask();
    }

    public void onChangeProjectTaskInDetail() {
        log.debug("onChangeProjectTaskInDetail. (selectedProjectTaskId: {})", selectedProjectTaskId);
        detail.setProjectTask(getObjById(projectTaskList, selectedProjectTaskId));
    }

    public void onChangeTaskInDetail() {
        log.debug("onChangeTaskInDetail. (selectTaskId: {})", selectedTaskId);
        detail.setTask(getObjById(taskList, selectedTaskId));
    }

    public void onChangeDuration() {
        log.trace("onChangeDuration.");
        chargeDurationButton = DateTimeUtil.durationToString(detail.getChargeDuration());
        log.debug("chargeDurationButton = {}", chargeDurationButton);
    }

    public void onChargeButtonClicked() {
        log.trace("onChargeButtonClicked.");
        detail.setChargeDuration(DateTimeUtil.stringToDuration(chargeDurationButton));
        log.debug("detail.chargeDuration = {}", detail.getChargeDuration());
    }

    private boolean isLeaveTask(TimeSheetDTO detail) {
        if (detail.getTask() == null) {
            return false;
        } else {
            return detail.getTask().getType() == TaskType.LEAVE;
        }
    }

    public void onSaveDetail() {
        log.debug("onSaveDetail. (detail: {})", detail);

        // validate task
        if (detail.getProjectTask() == null && detail.getTask() == null) {
            log.debug("not select project task.");
            FacesUtil.addError("Please select task!");
            return;
        }

        // if task are A002, A003 (leave)
        if (isLeaveTask(detail)) {
            log.debug("on leave task.");
        } else {
            // validate time-in, time-out
            if (detail.getTimeOut().before(detail.getTimeIn()) && detail.getSortOrder() == 1) {
                log.debug("time-out is before time-in. (time-in: {}, time-out: {})", detail.getTimeIn(), detail.getTimeOut());
                FacesUtil.addError("Time-out is before Time-in!");
                return;
            }

            if (detail.getTimeOut().equals(detail.getTimeIn()) && detail.getSortOrder() == 1) {
                log.debug("time-out is equal to time-in. (time-in: {}, time-out: {})", detail.getTimeIn(), detail.getTimeOut());
                FacesUtil.addError("Time-out is equal to Time-in!");
                return;
            }

            // validate charge hour
            if (detail.getChargeDuration().compareTo(Duration.ZERO) <= 0) {
                log.debug("not fill in charge duration.");
                FacesUtil.addError("Charge hours must greater than 0!");
                return;
            }
        }

        // validate description if no project
        if (detail.getProject() == null && detail.getDescription().isEmpty()) {
            log.debug("need to provide description if no project.");
            FacesUtil.addError("Please fill in description.");
            return;
        }

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(detail);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().saveRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.SUCCESS) {
                TimeSheetDTO result = serviceResponse.getResult();

                updateViewRecord(result);
                loadUtilization();
                PrimeFaces.current().executeScript("PF('timeSheetDlg').hide();");
            } else {
                FacesUtil.addError(serviceResponse.getMessage());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onReset() {
        log.debug("onReset. (detail: {})", detail);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(detail);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().resetRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.SUCCESS) {
                TimeSheetDTO result = serviceResponse.getResult();

                updateViewRecord(result);
                loadUtilization();
                PrimeFaces.current().executeScript("PF('timeSheetDlg').hide();");
            } else {
                FacesUtil.addError(serviceResponse.getMessage());
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onChangeTask(TimeSheetDTO timeSheet) {
        log.debug("onChangeTask. (selectedTaskId: {})", selectedProjectTaskId);
        timeSheet.setProjectTask(getObjById(projectTaskList, selectedProjectTaskId));
    }

    public void onAddRecord(TimeSheetDTO timeSheet) {
        log.debug("onAddRecord. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().newRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            TimeSheetDTO newTimeSheet = serviceResponse.getResult();
            log.debug("newTimeSheet: {}", newTimeSheet);
            timeSheetList.add(newTimeSheet);
            sortList(timeSheetList);
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    public void onDeleteRecord(TimeSheetDTO timeSheet) {
        log.debug("onDeleteRecord. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(userDetail.getUserId());
        Response response = apiService.getTimeSheetResource().deleteRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            timeSheetList.removeIf(t -> t.getId() == timeSheet.getId());
            loadUtilization();
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void sortList(List<TimeSheetDTO> list) {
        log.debug("sortList.");
//        list.sort((TimeSheetDTO o1, TimeSheetDTO o2) -> {
//            return Integer.compare(0, o2.getWorkDate().compareTo(o1.getWorkDate()));
//        });

        list.sort(Comparator.comparing(TimeSheetDTO::getWorkDate).thenComparing(TimeSheetDTO::getSortOrder));
    }

    public void onPreEdit(TimeSheetDTO timeSheet) {
        log.debug("onPreEdit. (timeSheet: {})", timeSheet);
        detail = timeSheet;
        selectedProjectId = 0;
        selectedProjectTaskId = 0;

        // change time sheet user
        loadProject();
        if (detail.getProject() != null) {
            selectedProjectId = detail.getProject().getId();
        } else {
            pjtSummary = Collections.emptyList();
        }

        loadProjectTask();
        if (detail.getProjectTask() != null) {
            selectedProjectTaskId = detail.getProjectTask().getId();
        }

        if (detail.getTask() != null) {
            selectedTaskId = detail.getTask().getId();
        } else {
            selectedTaskId = 0;
        }

        loadTimeSheetInfo(timeSheet);
    }

    public void onSave(TimeSheetDTO timeSheet) {
        log.debug("onSave. (timeSheet: {})", timeSheet);

        ServiceRequest<TimeSheetDTO> request = new ServiceRequest<>(timeSheet);
        request.setUserId(timeSheetUserId);
        Response response = apiService.getTimeSheetResource().saveRecord(request);
        if (response.getStatus() == 200) {
            ServiceResponse<TimeSheetDTO> serviceResponse = response.readEntity(new GenericType<ServiceResponse<TimeSheetDTO>>() {
            });

            if (serviceResponse.getApiResponse() == APIResponse.SUCCESS) {
                TimeSheetDTO result = serviceResponse.getResult();
                FacesUtil.addInfo("Saved. (" + DateTimeUtil.getDateStr(result.getWorkDate()) + ")");

                updateViewRecord(result);
                loadUtilization();
            }
        } else {
            log.debug("wrong response status! (status: {})", response.getStatus());
            FacesUtil.addError("wrong response from server!");
        }
    }

    private void updateViewRecord(TimeSheetDTO timeSheet) {
        log.debug("updateViewRecord. (timeSheet: {})", timeSheet);
        TimeSheetDTO t = null;
        for (TimeSheetDTO tmp : timeSheetList) {
            if (tmp.getId() == timeSheet.getId()) {
                t = tmp;
            }
        }
        timeSheetList.remove(t);
        timeSheetList.add(timeSheet);
        sortList(timeSheetList);
    }

    public long getSelectedTimeSheetId() {
        return selectedTimeSheetId;
    }

    public void setSelectedTimeSheetId(long selectedTimeSheetId) {
        this.selectedTimeSheetId = selectedTimeSheetId;
    }

    public List<TimeSheetDTO> getTimeSheetList() {
        return timeSheetList;
    }

    public void setTimeSheetList(List<TimeSheetDTO> timeSheetList) {
        this.timeSheetList = timeSheetList;
    }

    public List<TimeSheetDTO> getTimeSheetSummaryList() {
        return timeSheetSummaryList;
    }

    public void setTimeSheetSummaryList(List<TimeSheetDTO> timeSheetSummaryList) {
        this.timeSheetSummaryList = timeSheetSummaryList;
    }

    public List<ProjectDTO> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDTO> projectList) {
        this.projectList = projectList;
    }

    public long getSelectedProjectId() {
        return selectedProjectId;
    }

    public void setSelectedProjectId(long selectedProjectId) {
        this.selectedProjectId = selectedProjectId;
    }

    public List<ProjectTaskDTO> getProjectTaskList() {
        return projectTaskList;
    }

    public void setProjectTaskList(List<ProjectTaskDTO> projectTaskList) {
        this.projectTaskList = projectTaskList;
    }

    public long getSelectedProjectTaskId() {
        return selectedProjectTaskId;
    }

    public void setSelectedProjectTaskId(long selectedProjectTaskId) {
        this.selectedProjectTaskId = selectedProjectTaskId;
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

    public Date getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(Date currentMonth) {
        this.currentMonth = currentMonth;
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

    public TimeSheetDTO getDetail() {
        return detail;
    }

    public void setDetail(TimeSheetDTO detail) {
        this.detail = detail;
    }

    public List<TimeSheetDTO> getPjtSummary() {
        return pjtSummary;
    }

    public void setPjtSummary(List<TimeSheetDTO> pjtSummary) {
        this.pjtSummary = pjtSummary;
    }

    public List<UserDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDTO> userList) {
        this.userList = userList;
    }

    public long getTimeSheetUserId() {
        return timeSheetUserId;
    }

    public void setTimeSheetUserId(long timeSheetUserId) {
        this.timeSheetUserId = timeSheetUserId;
    }

    public Boolean getViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(Boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    public UtilizationResult getUtilization() {
        return utilization;
    }

    public void setUtilization(UtilizationResult utilization) {
        this.utilization = utilization;
    }

    public String getChargeDurationButton() {
        return chargeDurationButton;
    }

    public void setChargeDurationButton(String chargeDurationButton) {
        this.chargeDurationButton = chargeDurationButton;
    }

    public ExcelOptions getExportExcelOptions() {
        if (exportExcelOptions == null) {
            exportExcelOptions = new ExcelOptions("BOLD", "#FFFFFF", "#6666FF", "10", "", "#000000", "11");
        }
        return exportExcelOptions;
    }

    public void setExportExcelOptions(ExcelOptions exportExcelOptions) {
        this.exportExcelOptions = exportExcelOptions;
    }

    public String dataExportFileName() {
        String userName = null;
        if (userList != null) {
            for (UserDTO userDTO : userList) {
                if (userDTO.getId() == timeSheetUserId) {
                    userName = userDTO.getLoginName();
                    log.debug("dataExportFileName() = UserName({})", userName);
                }
            }
        }

        if (userName == null) {
            userName = userDetail.getUserName();
            log.debug("dataExportFileName() = CurrentLoggedInUser({})", userName);
        }

        String currentMonthString = DateTimeUtil.getDateStr(currentMonth, "MMM");
        return userName + "-timesheet-" + currentMonthString;
    }

    public String exportProject(TimeSheetDTO timeSheetDTO) {
        ProjectDTO project = timeSheetDTO.getProject();
        if (project == null) {
            return "-- No Project --";
        }

        return project.getCode() + " - " + project.getName();
    }

    public String exportTask(TimeSheetDTO timeSheetDTO) {
        TaskDTO task;

        ProjectTaskDTO projectTaskDTO = timeSheetDTO.getProjectTask();
        if (projectTaskDTO != null) {
            task = projectTaskDTO.getTask();
        } else {
            task = timeSheetDTO.getTask();
            if (task == null) {
                return "-- No Task --";
            }
        }

        return task.getCode() + " - " + task.getName();
    }

}
